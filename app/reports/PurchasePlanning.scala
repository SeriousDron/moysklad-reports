package reports

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import reports.group.ProductGroup
import services.MoyskladAPI
import services.moysklad.ProductRegistry
import services.moysklad.documents.RetailDemandRequest
import services.moysklad.reports.Stock
import services.moysklad.entity.Product

import scala.concurrent.{ExecutionContext, Future}

case class FolderStat[T](href: T, kinds: Int, stock: Int, sales: Int)

class PurchasePlanning @Inject() (moyskladApi: MoyskladAPI, productRegistry: ProductRegistry)(implicit ec: ExecutionContext) {

  def buildReport[T](grouping: ProductGroup[T]): Future[Seq[FolderStat[T]]] = {
    val stockStats: Future[Map[T, (Int, Int)]] = getStockStat(grouping)
    val salesStat: Future[Map[T, Int]] = getSalesStat(14)(grouping)

    for {
      stocks <- stockStats
      sales <- salesStat
    } yield {
      for {
        folder: T <- (stocks.keySet ++ sales.keySet).toSeq
      } yield FolderStat(folder, stocks(folder)._1, stocks(folder)._2, sales(folder))
    }
  }

  protected def getStockStat[T](grouping: ProductGroup[T]): Future[Map[T, (Int, Int)]] = {
    val stocks = moyskladApi.getStocks()
    stocks.map(response =>
      response.rows.groupBy(s => grouping.groupBy(productRegistry(s.meta.href)))
        .mapValues(groupStat).withDefaultValue((0, 0))
    )
  }

  protected def getSalesStat[T](period: Int)(grouping: ProductGroup[T]): Future[Map[T, Int]] = {
    val from: LocalDate = LocalDate.now().minus(period, ChronoUnit.DAYS)
    val sales = moyskladApi.getRetailDemand(new RetailDemandRequest(from))

    val folderQuantity: Future[Seq[(Product, Int)]] = sales.map(response =>
      response.rows.flatMap(
        retailDemand => {
          retailDemand.positions.rows.map(row => (productRegistry(row.assortment.meta.href), row.quantity))
        }
      )
    )

    folderQuantity map { fq =>
      fq.groupBy(pq => grouping.groupBy(pq._1)).mapValues(_.map(_._2).sum).withDefaultValue(0)
    }
  }

  private def groupStat(stocks: Seq[Stock]) : (Int, Int) = {
    val size = stocks.size
    val count = stocks.foldLeft(0)((sum, stock) => sum + stock.quantity)
    (size, count)
  }
}
