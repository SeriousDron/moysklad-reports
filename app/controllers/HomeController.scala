package controllers

import javax.inject._

import play.api.mvc._
import services.Moysklad
import services.moysklad.reports.Folder

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
      val meta = list.meta
      val grouped: Map[Option[Folder], Int] = list.rows.groupBy(_.folder).mapValues(items => items.foldLeft(0)((s, i) => s + i.quantity))
      Ok(meta.toString + grouped.toString())
    }
  }

}
