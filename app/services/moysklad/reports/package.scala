package services.moysklad

import play.api.libs.json._

/**
  * Created by Андрей on 05.06.2017.
  */
package object reports {

  implicit val folderReads: Reads[Folder] = Json.reads[Folder]
  implicit val stockReads: Reads[StockRow] = Json.reads[StockRow]
  implicit val stockResponseReads: Reads[StockResponse] = Json.reads[StockResponse]
}
