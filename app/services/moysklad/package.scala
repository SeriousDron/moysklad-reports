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
  abstract class PagedRequest(val limit: Int = 100, val offset: Int = 0) extends Request {

  }

  abstract class Response(val meta: Meta)
  case class PagedResponse[A](override val meta: MetaWithPaging, rows: Seq[A]) extends  Response(meta)

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
}
