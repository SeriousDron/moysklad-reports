package services.moysklad.reports

import services.moysklad._

/**
  * Created by Андрей on 05.06.2017.
  */
class Stock(override val request: StockRequest) extends PagedEntity[StockRow](request) {
  override val endpoint =  "/report/stock/all"
}

sealed trait StockMode { def name: String }
object StockMode {
  val All = new StockMode { val name = "all" }
  val PositiveOnly = new StockMode{ val name = "positiveOnly" }
}

class StockRequest extends PagedRequest {
  var store: Option[String] = None
  var stockMode: StockMode = StockMode.PositiveOnly
}

object StockRequest {
  def apply() : StockRequest = new StockRequest
}

case class Folder(name: String, pathName: Option[String])
case class StockRow(
                  code: String,
                  name: String,
                  quantity: Int,
                  folder: Option[Folder]
                )

case class StockResponse (
                          meta: MetaWithPaging,
                          rows: Seq[StockRow]
                        )