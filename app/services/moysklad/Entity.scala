package services.moysklad

/**
  * Created by Андрей on 05.06.2017.
  */
abstract class Entity[A <: Response] {
  def endpoint: String
  def queryString: Seq[(String, String)]
}

abstract class PagedEntity[E](var request: PagedRequest) extends Entity[PagedResponse[E]] with Cloneable {
  override def queryString: Seq[(String, String)] = {
    Seq(
      ("limit", request.limit.toString),
      ("offset", request.offset.toString)
    )
  }

  def next : this.type = {
    val nextReq: PagedRequest = request.next
    val cl = this.clone().asInstanceOf[this.type]
    cl.request = nextReq
    cl
  }
}