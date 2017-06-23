package services.moysklad

import javax.inject.{Inject, Singleton}

import services.MoyskladAPI
import services.moysklad.entity.Folder
import scala.concurrent.Await
import scala.concurrent.duration._

trait FolderRegistry extends Map[String, Folder]

@Singleton
class FolderRegistryImpl @Inject() (moyskladApi: MoyskladAPI) extends FolderRegistry {

  val response: PagedResponse[Folder] = Await.result(moyskladApi.getFolders(), Duration(30, SECONDS))
  val Folders: Map[String, Folder] = response.rows.map(p => (p.meta.href, p)).toMap

  override def get(key: String): Option[Folder] = Folders.get(key)
  override def iterator: Iterator[(String, Folder)] = Folders.iterator

  override def +[B1 >: Folder](kv: (String, B1)): Map[String, B1] = throw new UnsupportedOperationException("Folder registry is immutable")
  override def -(key: String): Map[String, Folder] = throw new UnsupportedOperationException("Folder registry is immutable")
}

