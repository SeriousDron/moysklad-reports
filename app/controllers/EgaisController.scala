package controllers

import java.time.LocalDate
import javax.inject._

import play.api.mvc._
import reports.EgaisStock
import services.moysklad.reports.StockRequest

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Success, Try}

/**
  * Created by seriousdron on 08.10.17.
  */
@Singleton
class EgaisController @Inject()(egaisStock: EgaisStock)(implicit exec: ExecutionContext) {

  def stock(dateStr: String):Action[AnyContent] = Action.async { implicit request =>
    stringToDate(dateStr).map(date =>
      egaisStock.buildReport(new StockRequest(moment = Some(date))).map(report => Results.Ok(
          views.html.egais.egaisCsv.render(report)
        ).as("text/plain")
      )
    ).getOrElse(Future.successful(Results.BadRequest("Incorrect date")))
  }

  private def stringToDate(dateStr: String): Try[LocalDate] =
    if (dateStr == "now")
      Success(LocalDate.now())
    else Try {
      LocalDate.parse(dateStr)
    }
}
