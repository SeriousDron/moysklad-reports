package services.moysklad.registry

import javax.inject.{Inject, Singleton}

import services.MoyskladAPI
import services.moysklad.entity.{Employee, Folder}


trait EmployeeRegistry extends Registry[Employee]

@Singleton
class EmployeeRegistryImpl @Inject() (moyskladApi: MoyskladAPI) extends Registry(moyskladApi.getEmployees()) with EmployeeRegistry {
}
