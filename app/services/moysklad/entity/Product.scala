package services.moysklad.entity

import play.api.libs.json.{Json, Reads}
import services.moysklad.{Meta, PagedRequest, PagedResponse, WrappedMeta, pagedResponseReads}

class ProductsRequest() extends PagedRequest[Product] {
  override def endpoint: String = "/entity/product"
}

object ProductsRequest {
  def apply(): ProductsRequest = new ProductsRequest
}

case class Product(meta: Meta, id: String, name: String, productFolder: Option[WrappedMeta], attributes: Option[Seq[Attribute]])

object Product {
  implicit val productReads: Reads[Product] = Json.reads[Product]
  implicit val productsResponseReads: Reads[PagedResponse[Product]] = pagedResponseReads[Product]()
}
