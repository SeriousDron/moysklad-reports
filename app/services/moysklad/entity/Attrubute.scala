package services.moysklad.entity

import play.api.libs.json._
import services.moysklad.Meta

sealed trait Attribute {
  def objType: String
  def name: String
  def value: Any
}

object Attribute {

  final val TypeBoolean = "boolean"
  final val TypeCustomEntity = "customentity"
  final val TypeString = "string"

  implicit val customEntityAttributeReads: Reads[CustomEntityValue] = Json.reads[CustomEntityValue]

  implicit val attributeReads: Reads[Attribute] = new Reads[Attribute] {
    override def reads(json: JsValue): JsResult[Attribute] = {
      val name = (json \ "name").as[String]
      val objType = (json \ "type").as[String]
      objType match {
        case TypeBoolean => JsSuccess(BooleanAttribute(objType, name, (json \ "value").as[Boolean]))
        case TypeCustomEntity => JsSuccess(CustomEntityAttribute(objType, name, (json \ "value").as[CustomEntityValue]))
        case TypeString => JsSuccess(StringAttribute(objType, name, (json \ "value").as[String]))
      }
    }
  }
}

case class CustomEntityValue(meta: Meta, name: String)
case class CustomEntityAttribute(objType: String, name: String, value: CustomEntityValue) extends Attribute
case class BooleanAttribute(objType: String, name: String, value: Boolean) extends Attribute
case class StringAttribute(objType: String, name: String, value: String) extends Attribute
