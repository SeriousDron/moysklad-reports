package services

import javax.inject.Inject

import play.api.libs.ws._

import scala.concurrent.{ExecutionContext, Future, Promise}
import com.google.inject.name.Named
import services.moysklad.Auth

trait Moysklad {
  def getStocks
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

  def getStocks = {
/*    print("username: ")
    println(auth.username)
    print("password: ")
    println(auth.password)*/
    val request = ws.url(baseUrl + "/report/stock/all")
        .withAuth(auth.username, auth.password, WSAuthScheme.BASIC)
    val response = request.execute()
    response map { response =>
      println(response.body)
    }
  }
}
