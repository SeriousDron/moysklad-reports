package services.moysklad.registry

import javax.inject.{Inject, Singleton}

import services.MoyskladAPI
import services.moysklad.entity.{Product, ProductMetadata}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait ProductRegistry extends Registry[Product] {
  def metadata: ProductMetadata
}

@Singleton
class ProductRegistryImpl @Inject() (moyskladApi: MoyskladAPI) extends Registry(moyskladApi.getProducts()) with ProductRegistry {
  private val metadataFuture: Future[ProductMetadata] = moyskladApi.getProductsMetadata()
  lazy val metadata: ProductMetadata = Await.result(metadataFuture, 5.seconds)
}
