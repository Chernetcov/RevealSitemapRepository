package world.reveal.sitemap
package models.cassandra

import entities.{SitemapCollectionData, StorySitemapData}
import models.SitemapRepository

import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID
import scala.jdk.CollectionConverters._

class CasSitemapRepository(implicit session: CqlSession) extends SitemapRepository{


  //private val cs: Resource[IO, CqlSession] = Resource.make(IO.pure(CqlSession.builder().build()))(session => IO(session.close()))

  /**
   * Get list of authors who write stories on selected language
   *
   * @param language is language connected with author
   * @return list of author uuid
   */
  override def authors(language: String): IO[Seq[UUID]] =
    IO.pure(session.execute("SELECT user_id FROM authors")
      .all
      .asScala
      .toSeq
      .map(_.getUuid("user_id"))
    )


  /**
   * Get categories slug names
   *
   * @return list of categories slug names
   */
override def categories(): IO[Seq[String]] =
  IO.pure(session.execute("SELECT name FROM interests")
    .all
    .asScala
    .toSeq
    .map(_.getString("name"))
  )


  /**
   * Get stories sitemap data
   *
   * @param language is stories language
   * @return list of stories data
   */
  override def stories(language: String): IO[Seq[StorySitemapData]] = {
    val statement = SimpleStatement.builder(
      "SELECT slug_name, update_time FROM story WHERE language = ?"
    ).addPositionalValue(language)
      .build()
    IO.pure(session.execute(statement)
      .all
      .asScala
      .toSeq
      .map(rs => StorySitemapData(
        rs.getString("slug_name"),
        LocalDateTime.ofInstant(rs.getInstant("update_time"), ZoneId.of("UTC"))
      ))
    )
  }



  /**
   * Get collections sitemap data
   *
   * @param language is collections language
   * @return list of collections data
   */
  override def collections(language: String): IO[Seq[SitemapCollectionData]] = {
    val statement = SimpleStatement.builder(
      "SELECT slug_name, edited, stories FROM collections WHERE language = ?"
    ).addPositionalValue(language)
      .build()
    IO.pure(session.execute(statement)
      .all
      .asScala
      .toSeq
      .map(rs => SitemapCollectionData(
        rs.getString("slug_name"),
        LocalDateTime.ofInstant(rs.getInstant("edited"), ZoneId.of("UTC")),
        rs.getSet("stories", classOf[String])
          .asScala
          .toList
      ))
    )
  }

  /**
   * Perform finish work like close connections, free memory
   */
  override def finish(): IO[Unit] = IO.pure {
    session.close()
  }
}
