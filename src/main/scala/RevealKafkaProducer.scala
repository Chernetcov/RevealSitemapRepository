package world.reveal.sitemap

import cats.effect.{ExitCode, IO, IOApp}
import fs2.kafka.consumer.KafkaConsumeChunk.CommitNow
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords, ProducerSettings}


object RevealKafkaProducer extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = {
    val stream = for {
      config <- ConfigLoader.getConfig
      producerSettings <- IO.pure(ProducerSettings[IO, String, String]
        .withBootstrapServers(config.kafkaSettings.server))
      _ <- KafkaProducer.stream(producerSettings)
        .evalMap(producer =>
          producer.produce(
            ProducerRecords.one(ProducerRecord(config.kafkaSettings.topic, "testKey", "testValue"))
          )
        ).compile.drain
    } yield ()
    stream.as(ExitCode.Success)
  }
}
