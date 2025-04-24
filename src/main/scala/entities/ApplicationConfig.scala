package world.reveal.sitemap
package entities

import pureconfig.{ConfigReader, ConvertHelpers}

object ServiceVersion extends Enumeration {
  type ServiceVersion = Value
  val Next, Cra = Value
}

case class DbConfig (
                      driver: String,
                      url: String,
                      username: String,
                      password: String,
                      maxPoolSize: Int
                    )

case class AwsConfig(
                    key: String,
                    secret: String,
                    region: String,
                    bucketName: String,
                    )

case class KafkaSettings(
                        topic: String,
                        group: String,
                        server: String
                        )

object ApplicationConfig {
  implicit val serviceVersionReader: ConfigReader[ServiceVersion.Value] = ConfigReader.fromString[ServiceVersion.Value](
    ConvertHelpers.catchReadError {
      case "cra" => ServiceVersion.Cra
      case "next" => ServiceVersion.Next
    }
  )
}


case class ApplicationConfig(
                            mode: ServiceVersion.ServiceVersion = ServiceVersion.Next,
                            languages: Seq[String],
                            path: String,
                            host: String = "https://reveal.world",
                            local: Boolean = false,
                            kafka: Boolean = false,
                            db: DbConfig,
                            aws: AwsConfig,
                            kafkaSettings: KafkaSettings
                            )
