package world.reveal.sitemap

import entities.ApplicationConfig

import cats.effect.IO
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import entities.ApplicationConfig.serviceVersionReader

object ConfigLoader {

  def getConfig: IO[ApplicationConfig] = {
    ConfigSource.defaultApplication.load[ApplicationConfig] match {
      case Right(config) =>
        check(config)
      case Left(error) =>
        IO.raiseError[ApplicationConfig](
          new Exception(s"Application config error: ${error.prettyPrint()}")
        )
    }
  }

  def check(config: ApplicationConfig): IO[ApplicationConfig] = {
    if (config.path.isBlank) {
      IO.raiseError[ApplicationConfig](new Exception("Path argument was not set"))
    } else {
      IO.pure(config)
    }
  }

}
