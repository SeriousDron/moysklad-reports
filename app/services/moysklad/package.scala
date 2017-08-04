package services

import java.time.chrono.IsoChronology
import java.time.{LocalDate, LocalDateTime}
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder, ResolverStyle}
import java.time.temporal.TemporalAccessor

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by Андрей on 04.06.2017.
  */
package object moysklad {

  case class Auth(username: String, password: String)

  class Meta(val href: String, val objType: String)
  object Meta {
    def apply(href: String, objType: String): Meta = new Meta(href, objType)
  }

  case class MetaWithPaging(override val href: String, override val objType: String, size: Int, limit: Int, offset: Int) extends Meta(href, objType)
  case class WrappedMeta(meta: Meta)

  abstract class Response(val meta: Meta)
  case class PagedResponse[A](override val meta: MetaWithPaging, rows: Seq[A]) extends  Response(meta) {
    def ++(other: PagedResponse[A]) : PagedResponse[A] = {
      if (other.meta.offset > meta.offset && other.meta.offset < (meta.offset + meta.limit)) throw new IllegalArgumentException
      val start = meta.offset min other.meta.offset
      val end = (meta.offset + meta.limit) max (other.meta.offset + other.meta.limit)
      val newMeta = MetaWithPaging(meta.href, meta.objType, meta.size, end - start, start)
      PagedResponse(newMeta, rows ++ other.rows)
    }

    def filter(p: (A) => Boolean): PagedResponse[A] = PagedResponse[A](meta, rows.filter(p))
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

  implicit val wrappedMetaReads: Reads[WrappedMeta] = Json.reads[WrappedMeta]

  def pagedResponseReads[E]()(implicit readsE: Reads[E]): Reads[PagedResponse[E]] = new Reads[PagedResponse[E]] {
    override def reads(json: JsValue): JsResult[PagedResponse[E]] = {
      val meta = (json \ "meta").as[MetaWithPaging]
      val rows = (json \ "rows").as[Seq[E]]
      JsSuccess(PagedResponse(meta, rows))
    }
  }

  implicit val momentReads: Reads[LocalDateTime] = localDateTimeReads(new DateTimeFormatterBuilder()
    .parseCaseInsensitive
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral(' ')
    .append(DateTimeFormatter.ISO_LOCAL_TIME)
    .toFormatter()
  )
}
