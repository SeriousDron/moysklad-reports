package reports.group

import services.moysklad.entity._
import services.moysklad.FolderRegistry


trait ProductGroup[T] {
  def groupBy(product: Product): T
  def groupName(id: T): String
}

class FolderGroup(folderRegistry: FolderRegistry) extends ProductGroup[Option[String]] {

  override def groupBy(product: Product): Option[String] = product.productFolder map (_.meta.href)

  override def groupName(id: Option[String]): String = {
    case None => "Без категории"
    case Some(folder) => folderRegistry(folder).name
  }
}

class CustomEntityAttributeGroup(name: String) extends ProductGroup[Option[String]] {

  override def groupBy(product: Product): Option[String] = product.attributes map { attrs =>
    attrs.filter({
      case CustomEntityAttribute(_, attrName, _) if name == attrName => true
      case _ => false
    }).head.name
  }

  override def groupName(id: Option[String]): String = id getOrElse "Не указано"
}
