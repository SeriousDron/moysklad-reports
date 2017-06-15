package services.moysklad

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

/**
  * Created by Андрей on 05.06.2017.
  */
abstract class Request[A <: Response] {
  def endpoint: String
  def queryString: Seq[(String, String)]

  private val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd 00:00:00")
  protected def formatDateTime(date: TemporalAccessor): String = dateFormatter.format(date)
}

abstract class PagedRequest[E](__limit: Int = 100, __offset: Int = 0) extends Request[PagedResponse[E]] with Cloneable {

  private var _limit: Int = __limit
  private var _offset: Int = __offset

  def limit(): Int = _limit
  def offset(): Int = _offset

  override def queryString: Seq[(String, String)] = {
    Seq(
      ("limit", _limit.toString),
      ("offset", _offset.toString)
    )
  }

  def next : this.type = {
    val cl: this.type = this.clone().asInstanceOf[this.type]
    cl._offset = this.offset + this.limit
    cl
  }
}