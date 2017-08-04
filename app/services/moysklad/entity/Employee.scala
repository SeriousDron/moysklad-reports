package services.moysklad.entity

import play.api.libs.json.{Json, Reads}
import services.moysklad.{Meta, PagedRequest}
import services.moysklad._

class EmployeeRequest extends PagedRequest[Employee] {
  override def endpoint: String = "/entity/employee"
}

object EmployeeRequest {
  def apply(): EmployeeRequest = new EmployeeRequest()
}

case class Employee(meta: Meta, name: String) extends Entity

object Employee {
  implicit val employeeReads: Reads[Employee] = Json.reads[Employee]
  implicit val employeeResponse: Reads[PagedResponse[Employee]] = pagedResponseReads[Employee]()
}