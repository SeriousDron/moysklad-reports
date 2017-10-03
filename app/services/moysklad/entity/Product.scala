package services.moysklad.entity

import play.api.libs.json.{Json, Reads}
import services.moysklad.{Meta, PagedRequest, PagedResponse, WrappedMeta, pagedResponseReads}

class ProductsRequest() extends PagedRequest[Product] {
  override def endpoint: String = "/entity/product"
}

object ProductsRequest {
  def apply(): ProductsRequest = new ProductsRequest
}

case class Alcoholic(
                    `type`: Option[Int],
                    strength: Option[Float],
                    volume: Option[Float]
                    )

case class Product(
                    meta: Meta,
                    id: String,
                    name: String,
                    productFolder: Option[WrappedMeta],
                    attributes: Option[Seq[Attribute]],
                    alcoholic: Option[Alcoholic]
                  ) extends Entity

object Product {
  implicit val alcoholicReads: Reads[Alcoholic] = Json.reads[Alcoholic]
  implicit val productReads: Reads[Product] = Json.reads[Product]
  implicit val productsResponseReads: Reads[PagedResponse[Product]] = pagedResponseReads[Product]()
}
