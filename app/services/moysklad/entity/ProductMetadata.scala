package services.moysklad.entity

import play.api.libs.json.{JsResult, JsValue, Json, Reads}
import services.moysklad._

/**
  * Created by Андрей on 03.07.2017.
  */
class ProductMetadataRequest extends Request[ProductMetadata]{
  override def endpoint: String = "/entity/product/metadata"
  override def queryString: Seq[(String, String)] = Seq()
}

object ProductMetadataRequest {
  def apply(): ProductMetadataRequest = new ProductMetadataRequest()
}

case class PriceType(name: String)
case class ProductMetadata(override val meta: Meta, attributes: Seq[Attribute], priceTypes: Seq[PriceType]) extends Response(meta)

object ProductMetadata {
  implicit val priceTypeReads: Reads[PriceType] = Json.reads[PriceType]
  implicit val productMetadataReads: Reads[ProductMetadata] = Json.reads[ProductMetadata]
}