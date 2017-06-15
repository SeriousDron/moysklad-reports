package services.moysklad.documents


import java.time.LocalDate

import play.api.libs.json.{Json, Reads}
import services.moysklad._

/**
  * Created by Андрей on 08.06.2017.
  */
class RetailDemandRequest(updatedFrom: LocalDate, updatedTo: LocalDate = LocalDate.now()) extends PagedRequest[RetailDemand](){
  override val endpoint: String = "/entity/retaildemand"

  override def queryString: Seq[(String, String)] = {
    super.queryString ++ Seq(
      ("updatedFrom", formatDateTime(updatedFrom)),
      ("updatedTo", formatDateTime(updatedTo)),
      ("expand", "positions,assortment")
    )
  }
}
//case class Assortment()
case class Position(meta: Meta, price: Int)
case class RetailDemand(id: String, positions: PagedResponse[Position])

object RetailDemand {
//  implicit val assortmentReads: Reads[Assortment] = Json.reads[Assortment]
  implicit val positionReads: Reads[Position] = Json.reads[Position]
  implicit val positionResponseReads: Reads[PagedResponse[Position]] = pagedResponseReads[Position]()
  implicit val retailDemandReads: Reads[RetailDemand] = Json.reads[RetailDemand]
  implicit val retailDemandResponseReads: Reads[PagedResponse[RetailDemand]] = pagedResponseReads[RetailDemand]()
}