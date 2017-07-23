package services.moysklad.registry

import javax.inject.{Inject, Singleton}

import services.MoyskladAPI
import services.moysklad.entity.Folder

trait FolderRegistry extends Registry[Folder]

@Singleton
class FolderRegistryImpl @Inject() (moyskladApi: MoyskladAPI) extends Registry(moyskladApi.getFolders()) with FolderRegistry {
}
