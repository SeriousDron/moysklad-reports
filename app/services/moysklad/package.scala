package services

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by Андрей on 04.06.2017.
  */
package object moysklad {

  case class Auth(username: String, password: String)

  class Meta(href: String, objType: String)
  object Meta {
    def apply(href: String, objType: String): Meta = new Meta(href, objType)
  }

  case class MetaWithPaging(href: String, objType: String, size: Int, limit: Int, offset: Int) extends Meta(href, objType)

  trait Request {

  }
  abstract class PagedRequest(__limit: Int = 100, __offset: Int = 0) extends Request with Cloneable {
    private var _limit: Int = __limit
    private var _offset: Int = __offset
    def limit(): Int = this._limit
    def offset(): Int = this._offset
    def next: this.type = {
      val cl: this.type = this.clone().asInstanceOf[this.type]
      cl._offset = this.offset + this.limit
      cl
    }
  }

  abstract class Response(val meta: Meta)
  case class PagedResponse[A](override val meta: MetaWithPaging, rows: Seq[A]) extends  Response(meta) {
    def ++(other: PagedResponse[A]) : PagedResponse[A] = {
      if (other.meta.offset > meta.offset && other.meta.offset < (meta.offset + meta.limit)) throw new IllegalArgumentException
      val start = meta.offset min other.meta.offset
      val end = (meta.offset + meta.limit) max (other.meta.offset + other.meta.limit)
      val newMeta = MetaWithPaging(meta.href, meta.objType, meta.size, end - start, start)
      PagedResponse(newMeta, rows ++ other.rows)
    }
  }

  implicit val metaReads: Reads[Meta] = (
    (JsPath \ "href").read[String] and
    (JsPath \ "type").read[String]
  ) (Meta.apply _)

  implicit val metaWithPagingReads: Reads[MetaWithPaging] = (
    (JsPath \ "href").read[String] and
    (JsPath \ "type").read[String] and
    (JsPath \ "size").read[Int] and
    (JsPath \ "limit").read[Int] and
    (JsPath \ "offset").read[Int]
  ) (MetaWithPaging.apply _)

  def pagedResponseReads[E]()(implicit readsE: Reads[E]): Reads[PagedResponse[E]] = new Reads[PagedResponse[E]] {
    override def reads(json: JsValue): JsResult[PagedResponse[E]] = {
      val meta = (json \ "meta").as[MetaWithPaging]
      val rows = (json \ "rows").as[Seq[E]]
      JsSuccess(PagedResponse(meta, rows))
    }
  }
}
