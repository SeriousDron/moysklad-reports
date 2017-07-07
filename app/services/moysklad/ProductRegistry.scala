package services.moysklad

import javax.inject.{Inject, Singleton}

import services.MoyskladAPI
import services.moysklad.entity.{Product, ProductMetadata}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

trait ProductRegistry extends Map[String, Product] {
  def metadata: ProductMetadata
}

@Singleton
class ProductRegistryImpl @Inject() (moyskladApi: MoyskladAPI) extends ProductRegistry {

  private val metadataFuture: Future[ProductMetadata] = moyskladApi.getProductsMetadata()
  private val productsFuture = moyskladApi.getProducts()

  lazy val metadata = Await.result(metadataFuture, 5.seconds)

  val response: PagedResponse[Product] = Await.result(productsFuture, 30.seconds)
  val products: Map[String, Product] = response.rows.map(p => (p.meta.href, p)).toMap

  override def get(key: String): Option[Product] = products.get(key)
  override def iterator: Iterator[(String, Product)] = products.iterator

  override def +[B1 >: Product](kv: (String, B1)): Map[String, B1] = throw new UnsupportedOperationException("Product registry is immutable")
  override def -(key: String): Map[String, Product] = throw new UnsupportedOperationException("Product registry is immutable")
}

