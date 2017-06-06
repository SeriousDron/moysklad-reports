package services

import javax.inject.Inject

import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future, Promise}
import play.api.libs.json._
import services.moysklad.{PagedResponse, _}
import services.moysklad.reports._
import services.moysklad.reports.Stock

trait Moysklad {
  def getStocks(request: StockRequest = StockRequest()): Future[PagedResponse[StockRow]]
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

  override def getStocks(req: StockRequest = StockRequest()): Future[PagedResponse[StockRow]] = {
    val stockRequest = new Stock(req)

    val wsRequest = ws.url(baseUrl + stockRequest.endpoint)
      .withAuth(auth.username, auth.password, WSAuthScheme.BASIC)
      .withHeaders(("Lognex-Pretty-Print-JSON", "true"))
      .withQueryString(stockRequest.queryString:_*)
    val wsResponse = wsRequest.execute()
    wsResponse map { response =>
      val jsonString: JsValue = Json.parse(response.body)
      jsonString.as[PagedResponse[StockRow]]
    }
  }
}
