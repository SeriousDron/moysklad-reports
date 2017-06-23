package reports

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import services.MoyskladAPI
import services.moysklad.ProductRegistry
import services.moysklad.documents.RetailDemandRequest
import services.moysklad.reports.Stock

import scala.concurrent.{ExecutionContext, Future}

case class FolderStat(href: Option[String], kinds: Int, stock: Int, sales: Int)

class PurchasePlanning @Inject() (moyskladApi: MoyskladAPI, productRegistry: ProductRegistry)(implicit ec: ExecutionContext) {

  def buildReport(folderId: String = ""): Future[Seq[FolderStat]] = {
    val stockStats: Future[Map[Option[String], (Int, Int)]] = getStockStat
    val salesStat: Future[Map[Option[String], Int]] = getSalesStat(14)

    for {
      stocks <- stockStats
      sales <- salesStat
    } yield {
      for {
        folder: Option[String] <- (stocks.keySet ++ sales.keySet).toSeq
      } yield FolderStat(folder, stocks(folder)._1, stocks(folder)._2, sales(folder))
    }
  }

  protected def getStockStat: Future[Map[Option[String], (Int, Int)]] = {
    val stocks = moyskladApi.getStocks()
    stocks.map(response =>
      response.rows.groupBy(_.folder.map(_.meta.href)).mapValues(groupStat).withDefaultValue((0, 0))
    )
  }

  protected def getSalesStat(period: Int): Future[Map[Option[String], Int]] = {
    val from: LocalDate = LocalDate.now().minus(period, ChronoUnit.DAYS)
    val sales = moyskladApi.getRetailDemand(new RetailDemandRequest(from))

    val folderQuantity: Future[Seq[(Option[String], Int)]] = sales.map(response =>
      response.rows.flatMap(
        retailDemand => {
          retailDemand.positions.rows.map(row => (productRegistry(row.assortment.meta.href).productFolder.map(_.meta.href), row.quantity))
        }
      )
    )

    folderQuantity map { fq =>
      fq.groupBy(_._1).mapValues(_.map(_._2).sum).withDefaultValue(0)
    }
  }

  private def groupStat(stocks: Seq[Stock]) : (Int, Int) = {
    val size = stocks.size
    val count = stocks.foldLeft(0)((sum, stock) => sum + stock.quantity)
    (size, count)
  }
}
