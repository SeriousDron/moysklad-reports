package reports.group

import javax.inject.{Inject, Singleton}

import services.moysklad.entity._
import services.moysklad.registry.{FolderRegistry, ProductRegistry}


trait ProductGroup[T] {
  def groupBy(product: Product): T
  def groupName(id: T): String
}

class FolderGroup(folderRegistry: FolderRegistry) extends ProductGroup[Option[String]] {

  override def groupBy(product: Product): Option[String] = product.productFolder map (_.meta.href)

  override def groupName(id: Option[String]): String = id match {
    case Some(folder) => folderRegistry(folder).name
    case _ => "Без категории"
  }
}

class CustomEntityAttributeGroup(name: String) extends ProductGroup[Option[String]] {

  override def groupBy(product: Product): Option[String] = product.attributes.flatMap( attr =>
    attr.collectFirst({
      case CustomEntityAttribute(_, attrName, value) if name == attrName => value.name
    })
  )

  override def groupName(id: Option[String]): String = id getOrElse "Не указано"
}
