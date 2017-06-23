package controllers

import javax.inject._

import play.api.mvc._
import reports.{FolderStat, PurchasePlanning}
import services.Moysklad
import services.moysklad.FolderRegistry

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(api: Moysklad, purchasePlanning: PurchasePlanning, folderRegistry: FolderRegistry)(implicit exec: ExecutionContext) extends Controller {

  def index = Action.async {

    purchasePlanning.buildReport().map(report => Ok(
      views.html.index.render(
        report.foldLeft(0)(_ + _.kinds),
        report.sortWith(folderStatLt).map(fs => {
          val st = FolderStat.unapply(fs).get
          val folder = st._1 match {
            case None => "Без категории"
            case Some(href) => folderRegistry(href).name
          }
          (folder, st._2, st._3, st._4)
        })
      )
    ))

    /*api.getStocks().map { list =>
      val size = list.meta.size
      val grouped: Map[String, Seq[Stock]] = list.rows.groupBy(_.folder match {
        case None => "Без группы"
        case Some(folder) => folder.name
      })
      val stat = grouped.mapValues(groupStat).toSeq.sortWith(_._2._2 < _._2._2)


    }*/
  }

  private def folderStatLt(fs1: FolderStat, fs2: FolderStat) : Boolean = {
    val predict1 = fs1.stock.toDouble / fs1.sales.toDouble
    val predict2 = fs2.stock.toDouble / fs2.sales.toDouble
    predict1 < predict2
  }

}
