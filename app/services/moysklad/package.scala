package services

import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by Андрей on 04.06.2017.
  */
package object moysklad {

  case class Auth(username: String, password: String)
  case class Meta(href: String, objType: String)
  case class Folder(name: String, pathName: Option[String])

  case class Stock(
                    code: String,
                    name: String,
                    quantity: Int,
                    folder: Folder
                  )

  case class StockResponse(
                            //context: Meta,
                            meta: Meta,
                            rows: Seq[Stock]
                          )

  implicit val metaReads: Reads[Meta] = (
    (JsPath \ "href").read[String] and
    (JsPath \ "type").read[String]
  )(Meta.apply _)

  implicit val folderReads: Reads[Folder] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "pathName").readNullable[String]
  )(Folder.apply _)

  implicit val stockReads: Reads[Stock] = Json.reads[Stock]
  implicit val stockResponseReads: Reads[StockResponse] = Json.reads[StockResponse]
}
