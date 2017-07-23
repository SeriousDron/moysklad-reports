import com.google.inject.AbstractModule
import java.time.Clock

import play.api.{Configuration, Environment}
import services._
import services.moysklad.registry.{FolderRegistry, FolderRegistryImpl, ProductRegistry, ProductRegistryImpl}
import services.moysklad.Auth

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module(
              environment: Environment,
              configuration: Configuration
            ) extends AbstractModule {

  override def configure() = {
    // Use the system clock as the default implementation of Clock
    bind(classOf[Clock]).toInstance(Clock.systemDefaultZone)
    // Ask Guice to create an instance of ApplicationTimer when the
    // application starts.
    bind(classOf[ApplicationTimer]).asEagerSingleton()
    // Set AtomicCounter as the implementation for Counter.
    bind(classOf[Counter]).to(classOf[AtomicCounter])

    bind(classOf[Moysklad]).to(classOf[MoyskladAPI])

    bind(classOf[Auth])
      .toInstance(Auth(
        configuration.getString("moysklad.user").getOrElse(""),
        configuration.getString("moysklad.pass").getOrElse("")
      ))

    bind(classOf[ProductRegistry]).to(classOf[ProductRegistryImpl]).asEagerSingleton()
    bind(classOf[FolderRegistry]).to(classOf[FolderRegistryImpl]).asEagerSingleton()
  }

}
