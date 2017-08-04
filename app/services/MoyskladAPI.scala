package services

import javax.inject.Inject

import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future, Promise}
import play.api.libs.json._
import services.moysklad.documents.{RetailDemand, RetailDemandRequest}
import services.moysklad.{PagedResponse, _}
import services.moysklad.reports._
import services.moysklad.reports.Stock
import services.moysklad.entity._

trait Moysklad {
  def getStocks(request: StockRequest = StockRequest()): Future[PagedResponse[Stock]]
  def getRetailDemand(request: RetailDemandRequest): Future[PagedResponse[RetailDemand]]
  def getProducts(request: ProductsRequest): Future[PagedResponse[Product]]
  def getProductsMetadata(request: ProductMetadataRequest): Future[ProductMetadata]
  def getFolders(request: FoldersRequest): Future[PagedResponse[Folder]]
  def getEmployees(request: EmployeeRequest): Future[PagedResponse[Employee]]
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

  override def getStocks(req: StockRequest = StockRequest()): Future[PagedResponse[Stock]] = {
    all(req)
  }

  override def getRetailDemand(req: RetailDemandRequest): Future[PagedResponse[RetailDemand]] = {
    all(req)
  }

  override def getProducts(req: ProductsRequest = ProductsRequest()): Future[PagedResponse[Product]] = {
    all(req)
  }

  override def getProductsMetadata(req: ProductMetadataRequest = ProductMetadataRequest()): Future[ProductMetadata] = {
    sendRequest(req)
  }

  override def getFolders(req: FoldersRequest = FoldersRequest()) : Future[PagedResponse[Folder]] = {
    all(req)
  }

  override def getEmployees(req: EmployeeRequest = EmployeeRequest()): Future[PagedResponse[Employee]] = {
    all(req)
  }

  private def all[A](request: PagedRequest[A])(implicit fjs: Reads[PagedResponse[A]]): Future[PagedResponse[A]] = {
    def addData(curData: Future[PagedResponse[A]], prevReq: PagedRequest[A]): Future[PagedResponse[A]] = {
      curData.flatMap(response => {
        if (response.meta.size <= response.meta.offset + response.meta.limit) {
          curData
        } else {
          val newReq = prevReq.next
          addData(sendRequest(newReq).map(response ++ _), newReq)
        }
      })
    }
    val first: Future[PagedResponse[A]] = sendRequest(request)
    addData(first, request)
  }

  private def sendRequest[A <: Response](request: Request[A])(implicit fjs: Reads[A]) : Future[A] = {
    val wsRequest = ws.url(baseUrl + request.endpoint)
      .withAuth(auth.username, auth.password, WSAuthScheme.BASIC)
      .withHeaders(("Lognex-Pretty-Print-JSON", "true"))
      .withQueryString(request.queryString:_*)
    val wsResponse = wsRequest.execute()
    wsResponse map { response =>
      val jsonString: JsValue = Json.parse(response.body)
      jsonString.as[A](fjs)
    }
  }
}
