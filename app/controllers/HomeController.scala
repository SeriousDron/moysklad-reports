package controllers

import javax.inject._

import play.api.mvc._
import reports.group.{CustomEntityAttributeGroup, FolderGroup, ProductGroup}
import reports.{FolderStat, PurchasePlanning}
import services.Moysklad
import services.moysklad.FolderRegistry
import play.api.i18n.{I18nSupport, MessagesApi}

import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
                                api: Moysklad,
                                purchasePlanning: PurchasePlanning,
                                folderRegistry: FolderRegistry,
                                val messagesApi: MessagesApi)
                              (implicit exec: ExecutionContext)
  extends Controller with I18nSupport  {

  def index:Action[AnyContent] = Action.async { implicit request =>

    val grouping = new FolderGroup(folderRegistry)
    buildReport(grouping)
  }

  def buy:Action[AnyContent] = Action.async { implicit request =>

    val grouping = new CustomEntityAttributeGroup("Закупочная категория")
    buildReport(grouping)
  }

  def manufacture:Action[AnyContent] = Action.async { implicit request =>

    val grouping = new CustomEntityAttributeGroup("Производитель")
    buildReport(grouping)
  }

  private def buildReport[T](grouping: ProductGroup[T])(implicit request: Request[AnyContent]) = {
    purchasePlanning.buildReport(grouping).map(report => Ok(
      views.html.index.render(
        report.foldLeft(0)(_ + _.kinds),
        report.sortWith(folderStatLt).map(fs => {
          val st = FolderStat.unapply(fs).get
          val folder = grouping.groupName(st._1)
          (folder, st._2, st._3, st._4)
        }),
        request
      )
    ))
  }

  private def folderStatLt[T](fs1: FolderStat[T], fs2: FolderStat[T]) : Boolean = {
    val predict1 = fs1.stock.toDouble / fs1.sales.toDouble
    val predict2 = fs2.stock.toDouble / fs2.sales.toDouble
    predict1 < predict2
  }

}
