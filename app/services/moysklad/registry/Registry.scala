package services.moysklad.registry

import services.moysklad.PagedResponse
import services.moysklad.entity.Entity

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._


abstract class Registry[E <: Entity](resultFuture: Future[PagedResponse[E]]) extends Map[String, E] {

  val response: PagedResponse[E] = Await.result(resultFuture, 30.seconds)
  val items: Map[String, E] = response.rows.map(p => (p.meta.href, p)).toMap

  override def get(key: String): Option[E] = { //Ignoring query params
  val qPos = key.indexOf('?')
    val href = if (qPos != -1) key.substring(0, qPos) else key
    items.get(href)
  }

  override def iterator: Iterator[(String, E)] = items.iterator

  override def +[B1 >: E](kv: (String, B1)): Map[String, B1] = throw new UnsupportedOperationException("Entity registry is immutable")
  override def -(key: String): Map[String, E] = throw new UnsupportedOperationException("Entity registry is immutable")
}
