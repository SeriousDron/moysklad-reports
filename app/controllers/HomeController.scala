package controllers

import javax.inject._

import play.api.mvc._
import services.Moysklad
import services.moysklad.reports.{Folder, Stock}

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (api: Moysklad)(implicit exec: ExecutionContext) extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action.async {
    api.getStocks().map { list =>
      val size = list.meta.size
      val grouped: Map[String, Seq[Stock]] = list.rows.groupBy(_.folder match {
        case None => "Без группы"
        case Some(folder) => folder.name
      })
      val stat = grouped.mapValues(groupStat).toSeq.sortWith(_._2._2 < _._2._2)

      Ok(views.html.index.render(size, stat))
    }
  }

  private def groupStat(stocks: Seq[Stock]) : (Int, Int) = {
    val size = stocks.size
    val count = stocks.foldLeft(0)((sum, stock) => sum + stock.quantity)
    (size, count)
  }
}
