package services.moysklad

import play.api.libs.json._

/**
  * Created by Андрей on 05.06.2017.
  */
package object reports {

  implicit val stockReads: Reads[Stock] = Json.reads[Stock]
  implicit val stockResponseReads: Reads[PagedResponse[Stock]] = pagedResponseReads[Stock]()
}
