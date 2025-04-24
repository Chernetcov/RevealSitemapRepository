package world.reveal.sitemap
package models.postgres

import models.SitemapRepository

import cats.effect.{IO, Resource}
import doobie.Transactor
import doobie.implicits._
import doobie.postgres.implicits._
import entities.{DbConfig, PublicationStatus, SitemapCollectionData, StorySitemapData}

import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

import java.util.UUID

/**
 * Loads sitemap data from PostgreSQL database
 * @param dbConfig is database configuration options
 */
class PgSitemapRepository(dbConfig: DbConfig) extends SitemapRepository{

  private val transactor: Resource[IO, HikariTransactor[IO]] =
    for {
      hikariConfig <- Resource.pure {
        val config = new HikariConfig()
        config.setDriverClassName(dbConfig.driver)
        config.setJdbcUrl(dbConfig.url)
        config.setUsername(dbConfig.username)
        config.setPassword(dbConfig.password)
        config
      }
      xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig)
    } yield xa

  /**
   * Get list of authors who write stories on selected language
   * @param language is language connected with author
   * @return list of author uuid
   */
  override def authors(language: String): IO[Seq[UUID]] = {
    transactor.use{ xa =>
      sql"""SELECT user_uuid FROM users u JOIN
           | (SELECT count(*) AS storiesCount, author_id FROM story s
           | WHERE s.language_id IN (SELECT id FROM language WHERE code = $language) GROUP BY author_id) AS stories
           | ON stories.author_id = u.id
           | WHERE u.user_role > 1 ORDER BY storiesCount DESC
           |""".stripMargin
        .query[UUID]
        .to[Seq]
        .transact(xa)
    }
  }

  /**
   * Get categories slug names
   * @return list of categories slug names
   */
  override def categories(): IO[Seq[String]] = {
    transactor.use{ xa =>
      sql"""SELECT name FROM interests""".stripMargin
        .query[String]
        .to[Seq]
        .transact(xa)
    }
  }

  /**
   * Get stories sitemap data
   *
   * @param language is stories language
   * @return list of stories data
   */
  override def stories(language: String): IO[Seq[StorySitemapData]] = {
    transactor.use{ xa =>
      sql"""SELECT slug_name, update_time FROM story s
           | JOIN language l ON s.language_id = l.id
           | WHERE status = ${PublicationStatus.Published.id}
           | and l.code=$language"""
        .stripMargin
        .query[StorySitemapData]
        .to[Seq]
        .transact(xa)
    }
  }

  /**
   * Get collections sitemap data
   *
   * @param language is collections language
   * @return list of collections data
   */
  override def collections(language: String): IO[Seq[SitemapCollectionData]] = {
    transactor.use{ xa =>
      sql"""SELECT c.slug_name, edited,
           | ARRAY(SELECT s.slug_name FROM story_collection sc JOIN (SELECT id, slug_name, status FROM story) AS s ON sc.story_id = s.id
           | WHERE s.status = ${PublicationStatus.Published.id} AND collection_id = c.id) as stories
           | FROM collections c
           | WHERE c.status = ${PublicationStatus.Published.id}
           | AND c.language_id = (SELECT id FROM language WHERE code = $language LIMIT 1)"""
        .stripMargin
        .query[SitemapCollectionData]
        .to[Seq]
        .transact(xa)
    }
  }
}
