package services.moysklad.entity

import play.api.libs.json.{Json, Reads}
import services.moysklad.{Meta, PagedRequest, PagedResponse, pagedResponseReads}

class FoldersRequest extends PagedRequest[Folder] {
  override def endpoint: String = "/entity/productfolder"
}

object FoldersRequest {
  def apply(): FoldersRequest = new FoldersRequest
}

case class Folder(meta: Meta, name: String, pathName: Option[String])

object Folder {
  implicit val folderReads: Reads[Folder] = Json.reads[Folder]
  implicit val folderResponseReads: Reads[PagedResponse[Folder]] = pagedResponseReads[Folder]()
}