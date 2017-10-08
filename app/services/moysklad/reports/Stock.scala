package services.moysklad.reports

import java.time.LocalDate

import services.moysklad._
import services.moysklad.entity.Folder
import services.moysklad.reports.StockMode._

/**
  * Created by Андрей on 05.06.2017.
  */
case class StockRequest(store: Option[String] = None, stockMode: Option[StockMode] = None, moment: Option[LocalDate] = None) extends PagedRequest[Stock]() {
  override val endpoint =  "/report/stock/all"

  override def queryString: Seq[(String, String)] = {
    val withStore:Seq[(String, String)] = store.foldLeft(super.queryString)(_ :+ ("store.id", _))
    val withMode = stockMode.foldLeft(withStore)(_ :+ ("stockMode", _))
    moment.map(formatDateTime).foldLeft(withMode)(_ :+ ("moment", _))
  }
}

object StockRequest {
  def apply() : StockRequest = new StockRequest
}

object StockMode {
  type StockMode = String
  val All = "all"
  val PositiveOnly = "positiveOnly"
  val NonEmpty = "nonEmpty"
}

trait Mode extends Enumeration

case class Stock(
                  meta: Meta,
                  name: String,
                  quantity: Int,
                  folder: Option[Folder]
                )

case class StockResponse (
                          meta: MetaWithPaging,
                          rows: Seq[Stock]
                        )