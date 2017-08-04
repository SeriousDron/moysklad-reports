package reports

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

import services.MoyskladAPI
import services.moysklad.documents.{RetailDemand, RetailDemandRequest}
import services.moysklad.entity.Employee
import services.moysklad.registry.{EmployeeRegistry, ProductRegistry}

import scala.concurrent.{ExecutionContext, Future}

case class EmployeeStat(employee: Employee, avgQuantity: Double, avgSum: Int, totalSales: Int, totalSum: Int)

class EmployeePerformance @Inject()(moyskladApi: MoyskladAPI, employeeRegistry: EmployeeRegistry)(implicit ec: ExecutionContext) {

  final val reportPeriod = 14

  def buildReport(): Future[Seq[EmployeeStat]] = {
    moyskladApi.getRetailDemand(new RetailDemandRequest(LocalDate.now().minus(reportPeriod, ChronoUnit.DAYS))) map { response =>
      val byEmployee = response.rows groupBy (_.owner.meta.href)
      byEmployee.mapValues(employeeSales2Stat).toSeq.map { t =>
        EmployeeStat(employeeRegistry(t._1), t._2._1, t._2._2, t._2._3, t._2._4)
      }
    }
  }

  protected def employeeSales2Stat(s: Seq[RetailDemand]) : (Double, Int, Int, Int) = {
    val total = s.size
    val bottles = s.foldLeft(0)(_ + _.positions.rows.foldLeft(0)(_ + _.quantity))
    val sum = s.foldLeft(0)(_ + _.sum)
    val avgBottles = bottles.toDouble / total.toDouble
    val avgSum = sum / total
    (avgBottles, avgSum, total, sum)
  }
}
