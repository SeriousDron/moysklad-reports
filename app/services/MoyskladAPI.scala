package services

import javax.inject.Inject

import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future, Promise}
import play.api.libs.json._
import services.moysklad._


trait Moysklad {
  def getStocks: Future[StockResponse]
}


/**
  * Created by Андрей on 01.06.2017.
  */
class MoyskladAPI @Inject() (ws: WSClient,
                             auth: Auth)
                            (implicit exec: ExecutionContext)
extends Moysklad
{
  private val baseUrl = "https://online.moysklad.ru/api/remap/1.1"

  override def getStocks: Future[StockResponse] = {
/*    print("username: ")
    println(auth.username)
    print("password: ")
    println(auth.password)*/
    val request = ws.url(baseUrl + "/report/stock/all")
        .withAuth(auth.username, auth.password, WSAuthScheme.BASIC)
        .withHeaders(("Lognex-Pretty-Print-JSON", "true"))
    val response = request.execute()
    response map { response =>
      val jsonString: JsValue = Json.parse(response.body)
      jsonString.as[StockResponse]
    }
  }
}
