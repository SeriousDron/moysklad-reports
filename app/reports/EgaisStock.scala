package reports

import javax.inject.Inject

import services.Moysklad
import services.moysklad.entity.{Product, StringAttribute}
import services.moysklad.registry.ProductRegistry
import services.moysklad.reports.Stock

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by seriousdron on 07.10.17.
  */
class EgaisStock @Inject()(moysklad: Moysklad, productRegistry: ProductRegistry)(implicit ec: ExecutionContext) {
  type EgaisKey = (String, String)
  type StockProduct = (Stock, Product)

  def buildReport(): Future[Map[EgaisKey, Float]] = {
    moysklad.getStocks().map(stocks => {
      val alcoholicProductsStock: Seq[StockProduct] = stocks.rows.map(s => (s, productRegistry(s.meta.href))).filter(_._2.alcoholic.isDefined)
      val grouped: Map[EgaisKey, Seq[StockProduct]] = alcoholicProductsStock.groupBy(stockToKey)
      grouped.mapValues(_.map(stockProductToDeciliters).sum)
    })
  }

  def stockToKey(sp: StockProduct): EgaisKey = {
    val product = sp._2
    val manufacture = product.attributes.flatMap(_.collectFirst({
      case StringAttribute(_, EgaisStock.manufactureField, value) => value
    })).getOrElse("None")
    val alcoholType = product.alcoholic.flatMap(_.`type`).map(_.toString).getOrElse("None")
    (manufacture, alcoholType)
  }

  def stockProductToDeciliters(sp: StockProduct): Float = {
    val quantity: Int = sp._1.quantity
    val volume: Float = sp._2.alcoholic.flatMap(_.volume).getOrElse(0f)
    quantity * volume / 10f
  }
}

object EgaisStock {
  final val manufactureField = "ЕГАИСКодПроизводителя"
}
