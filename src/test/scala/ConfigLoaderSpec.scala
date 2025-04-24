package world.reveal.sitemap

import entities.{ApplicationConfig, AwsConfig, DbConfig, KafkaSettings}

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

class ConfigLoaderSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers{

  private val dbConfig = DbConfig("", "", "", "", 2)
  private val awsConfig = AwsConfig("","","","")
  private val kafkaSettings = KafkaSettings(
    topic = "",
    group = "",
    server = ""
  )
  private val path = "/tmp/sitemap.xml"

  "ConfigLoader" - {
    "check path param" in {
      val config = ApplicationConfig(
        languages = Seq(),
        path = path,
        db = dbConfig,
        aws = awsConfig,
        kafkaSettings = kafkaSettings
      )
      ConfigLoader.check(config)
        .map(settings => assert(settings.path == path)
        ).handleError(ex => fail(ex.getMessage))
    }

    "return exception if path was not set" in {
      val config = ApplicationConfig(
        languages = Seq(),
        path = "",
        db = dbConfig,
        aws = awsConfig,
        kafkaSettings = kafkaSettings
      )
      ConfigLoader.check(config)
        .map(_ => fail("Empty path return value")
        ).handleError(_ => succeed)
    }

//    "return exception if db url settings was not set" in {
//      val config = ApplicationConfig(
//        languages = Seq(),
//        path = path,
//        db = DbConfig(
//          driver = "org.postgresql.Driver",
//          url = "",
//          username = "postgres",
//          password = "postgres",
//          2
//        ),
//        aws = awsConfig
//      )
//      ConfigLoader.check(config)
//        .map(settings => assertThrows(new Exception("ALLAL"))
//        ).handleError(ex => fail(ex.getMessage))
//    }
  }
}