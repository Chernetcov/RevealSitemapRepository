package world.reveal.sitemap

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.Logger
import fs2.kafka.{AutoOffsetReset, ConsumerRecord, ConsumerSettings, KafkaConsumer}



object RevealSitemapConsumer extends IOApp {

  private val logger = Logger(getClass.getName)

  private def processRecord(record: ConsumerRecord[String, String]): IO[Unit] = {
    logger.info(s"Event come ${record.key} -> ${record.value}")
    SitemapApplication.mainIO
  }


  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      config <- ConfigLoader.getConfig
      consumerSettings <- IO.pure(ConsumerSettings[IO, String, String]
        .withBootstrapServers(config.kafkaSettings.server)
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withGroupId(config.kafkaSettings.group)
        .withEnableAutoCommit(true))
      _ <- KafkaConsumer.stream(consumerSettings)
        .subscribeTo(config.kafkaSettings.topic)
        .records
        .evalMap { committable =>
          processRecord(committable.record)
        }.compile
        .drain
    } yield ()
    stream.as(ExitCode.Success)
  }
}