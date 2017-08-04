package controllers

import java.time.LocalDate
import javax.inject._

import play.api.mvc._
import reports.EmployeePerformance
import services.moysklad.documents.RetailDemandRequest
import services.{Counter, Moysklad}

import scala.concurrent.ExecutionContext

@Singleton
class SalesController @Inject()(api: Moysklad, employeePerformance: EmployeePerformance)(implicit exec: ExecutionContext) extends Controller {

  def perHour = Action.async { implicit request =>
    api.getRetailDemand(new RetailDemandRequest(LocalDate.of(2017, 4, 1))).map(response => {
      val total = response.rows.size
      val res = response.rows.groupBy(_.moment.getHour).mapValues(_.size).toSeq.sortWith(_._1 < _._1).mkString("\n")
      Ok(s"$res\n Total: $total")
    })
  }

  def perEmployee = Action.async { implicit request =>
    employeePerformance.buildReport().map { res =>
      Ok(views.html.sales.perEmployee(res))
    }
  }
}
