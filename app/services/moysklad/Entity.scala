package services.moysklad

/**
  * Created by Андрей on 05.06.2017.
  */
abstract class Entity[A <: Response] {
  def endpoint: String
  def queryString: Seq[(String, String)]
}

abstract class PagedEntity[E](val request: PagedRequest) extends Entity[PagedResponse[E]] {
  override def queryString: Seq[(String, String)] = {
    Seq(
      ("limit", request.limit.toString),
      ("offset", request.offset.toString)
    )
  }
}