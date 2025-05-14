package world.reveal.sitemap

import entities.{ApplicationConfig, DbConfig, RepositoryType}
import models.SitemapRepository
import models.cassandra.CasSitemapRepository
import models.postgres.PgSitemapRepository

import cats.effect.{IO, Resource}
import com.datastax.oss.driver.api.core.CqlSession
import com.zaxxer.hikari.HikariConfig
import doobie.hikari.HikariTransactor

object RepositoryCreator {

  private def createPostgresRepository(dbConfig: DbConfig): SitemapRepository = {
    implicit val transactor: Resource[IO, HikariTransactor[IO]] =
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
    new PgSitemapRepository()
  }

  private def createCassandraRepository(): SitemapRepository = {
    implicit val session: CqlSession = CqlSession.builder().build()
    new CasSitemapRepository()
  }

  def create(config: ApplicationConfig): SitemapRepository = {
    config.repository match {
      case RepositoryType.Postgres => createPostgresRepository(config.db)
      case RepositoryType.Cassandra => createCassandraRepository()
      }
    }

}
