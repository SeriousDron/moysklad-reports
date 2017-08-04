package controllers

import java.time.LocalDate
import javax.inject._

import play.api.mvc._
import services.moysklad.documents.RetailDemandRequest
import services.{Counter, Moysklad}

import scala.concurrent.ExecutionContext

/**
 * This controller demonstrates how to use dependency injection to
 * bind a component into a controller class. The class creates an
 * `Action` that shows an incrementing count to users. The [[Counter]]
 * object is injected by the Guice dependency injection system.
 */
@Singleton
class SalesController @Inject()(api: Moysklad)(implicit exec: ExecutionContext) extends Controller {

  /**
    * Create an action that responds with the [[Counter]]'s current
    * count. The result is plain text. This `Action` is mapped to
    * `GET /sales/per-hour` requests by an entry in the `routes` config file.
    */
  def perHour = Action.async { implicit request =>
    api.getRetailDemand(new RetailDemandRequest(LocalDate.of(2017, 4, 1))).map(response => {
      val total = response.rows.size
      val res = response.rows.groupBy(_.moment.getHour).mapValues(_.size).toSeq.sortWith(_._1 < _._1).mkString("\n")
      Ok(s"${res}\n Total: $total")
    })
  }

}
