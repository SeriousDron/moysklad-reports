package services.moysklad.entity

import play.api.libs.json.{Json, Reads}
import services.moysklad.{Meta, PagedRequest, pagedResponseReads}
import services.moysklad.reports.Folder

class ProductsRequest() extends PagedRequest[Product] {
  override def endpoint: String = "/entity/product"
  override def queryString: Seq[(String, String)] = super.queryString ++ Seq(("expand", "productFolder"))
}

object ProductsRequest {
  def apply(): ProductsRequest = new ProductsRequest
}

case class Product(meta: Meta, id: String, name: String, productFolder: Option[Folder])

object Product {
  implicit val productReads: Reads[Product] = Json.reads[Product]
  implicit val productsResponseReads = pagedResponseReads[Product]()
}
