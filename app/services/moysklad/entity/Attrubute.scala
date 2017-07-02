package services.moysklad.entity

import play.api.libs.json._
import services.moysklad.Meta

sealed trait Attribute {
  def objType: String
  def name: String
  def value: Any
}

object Attribute {

  implicit val customEntityAttributeReads: Reads[CustomEntityValue] = Json.reads[CustomEntityValue]

  implicit val attributeReads: Reads[Attribute] = new Reads[Attribute] {
    override def reads(json: JsValue): JsResult[Attribute] = {
      val name = (json \ "name").as[String]
      val objType = (json \ "type").as[String]
      objType match {
        case "boolean" => JsSuccess(BooleanAttribute(objType, name, (json \ "value").as[Boolean]))
        case "customentity" => JsSuccess(CustomEntityAttribute(objType, name, (json \ "value").as[CustomEntityValue]))
      }
    }
  }
}

case class CustomEntityValue(meta: Meta, name: String)
case class CustomEntityAttribute(objType: String, name: String, value: CustomEntityValue) extends Attribute
case class BooleanAttribute(objType: String, name: String, value: Boolean) extends Attribute
