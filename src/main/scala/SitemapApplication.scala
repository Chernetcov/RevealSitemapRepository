package world.reveal.sitemap

import entities.{ApplicationConfig, ServiceVersion, SitemapManager}

import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.Logger

object SitemapApplication extends IOApp {

  private val logger = Logger(getClass.getName)

  private def createManager(config: ApplicationConfig) = {
    IO.pure(SitemapManager(
      RepositoryCreator.create(config),
      config.mode match {
        case ServiceVersion.Next => new NextSitemapCreator()
        case ServiceVersion.Cra => new CraSitemapCreator()
      },
      if (!config.local) {
        new S3SitemapFileWriter(config.aws)
      }
      else {
        new LocalSitemapFileWriter()
      }
    ))
  }

  val mainIO: IO[Unit] = for {
    config <- ConfigLoader.getConfig
    manager <- createManager(config).map{t => logger.info("manager created")
      t
    }
    _ <- SitemapService.generateSitemap(config).run(manager)
  } yield ()

  override def run(args: List[String]): IO[ExitCode] = {
    mainIO.handleError(ex => logger.error(ex.getMessage))
      .map(_ => ExitCode.Success)
  }

  /**
   * Function runs on AWS Lambda
   */
  def createSitemap(): Unit = {
    mainIO.handleError(ex => logger.error(ex.getMessage))
      .unsafeRunSync()
  }

}
