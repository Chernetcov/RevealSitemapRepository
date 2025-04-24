package world.reveal.sitemap

import entities.{AwsConfig, SitemapXml}

import cats.effect.{IO, Resource}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, StaticCredentialsProvider}
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import java.io.StringWriter
import java.nio.file.{Files, Paths}

/**
 *  Save sitemap
 */
trait SitemapFileWriter {
  /**
   * Write file
   * @param sitemap is sitemap data to save
   * @param path is path to save sitemap
   * @return
   */
  def write(sitemap: SitemapXml, path: String): IO[Unit]
}


/**
 * Save sitemap to local file system
 */
class LocalSitemapFileWriter extends SitemapFileWriter {

  /**
   *
   * @param path is local path to create file
   * @return
   */
  private def createFile(path: String): IO[Unit] = {
    val nioPaths = Paths.get(path)
    IO {
      Files.createDirectories(nioPaths.getParent)
      if (!Files.exists(nioPaths)) {
        Files.createFile(nioPaths)
      }
    }
  }

  /**
   * Write file
   * @param sitemap is sitemap data to save
   * @param path is local path to save sitemap (including filename)
   * @return
   */
  override def write(sitemap: SitemapXml, path: String): IO[Unit] = {
    createFile(path).map(_ =>
      scala.xml.XML.save(path, sitemap.toXml, "UTF-8", xmlDecl = true, null)
    )
  }
}


/**
 * Saves sitemap to AWS S3
 * @param config is AWS connection config
 */
class S3SitemapFileWriter(config: AwsConfig) extends SitemapFileWriter {

  /**
   * Write file to S3 bucket
   * @param sitemap is sitemap data to save
   * @param path is path on the bucket to save sitemap
   * @return
   */
  override def write(sitemap: SitemapXml, path: String): IO[Unit] = {
    val awsCredentials = AwsBasicCredentials.create(config.key, config.secret)
    Resource.make(
      IO(S3Client.builder()
      .region(Region.of(config.region))
      .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
      .build())
    ) (s3 =>
      IO(s3.close()).handleErrorWith(_ => IO.unit)
    ).use {s3 =>
      IO{
        val putOb = PutObjectRequest.builder()
          .bucket(config.bucketName)
          .key(path)
          .contentType("text/xml")
          .build()
        val encoding = "UTF-8"
        val writer = new StringWriter()
        scala.xml.XML.write(writer, sitemap.toXml, encoding, xmlDecl = true, null)
        s3.putObject(putOb, RequestBody.fromString(writer.toString))
      }
    }
  }

}
