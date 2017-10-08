package controllers

import javax.inject._

import play.api.mvc._
import reports.EgaisStock

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by seriousdron on 08.10.17.
  */
@Singleton
class EgaisController @Inject()(egaisStock: EgaisStock)(implicit exec: ExecutionContext) {

  def stock():Action[AnyContent] = Action.async { implicit request =>
    egaisStock.buildReport().map(report => Results.Ok(
        views.html.egais.egaisCsv.render(report)
      ).as("text/plain")
    )
  }
}
