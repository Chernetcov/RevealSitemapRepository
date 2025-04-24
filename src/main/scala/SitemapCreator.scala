package world.reveal.sitemap

import entities.{Sitemap, SitemapElementChangeFreq, SitemapXml, SitemapXmlElement}

import cats.effect.IO

import java.time.{ZoneId, ZonedDateTime}

/**
 * Generates xml structure based on the sitemap data
 * Is necessary because of different sitemaps for old (CRA + Play Framework 2)
 * and new (Next.js + Play Framework 3) services
 */
trait SitemapCreator {
  /**
   *
   * @param host is base host
   * @param sitemap is sitemap data
   * @return
   */
  def create(host: String, sitemap: Sitemap): IO[SitemapXml]
}

/**
 * Sitemap generation for new service (Next.js + Play Framework 3)
 */
class NextSitemapCreator extends SitemapCreator {

  override def create(host: String, sitemap: Sitemap): IO[SitemapXml] = {
    IO {
      val elements = sitemap.languages.map(lang =>
        SitemapXmlElement(
          s"$host/$lang/", ZonedDateTime.now(ZoneId.of("UTC")), SitemapElementChangeFreq.Weekly
        )
      )  ++ sitemap.authors.flatMap(sm => sm.authors.map(author =>
        SitemapXmlElement(
          s"$host/${sm.language}/author/$author/", null, null
        )
      )) ++ sitemap.languages.flatMap(language => sitemap.categories.map(category =>
        SitemapXmlElement(
          s"$host/$language/category/$category/", null, null
        )
      )) ++ sitemap.stories.flatMap(sm => sm.stories.map(story =>
        SitemapXmlElement(
          s"$host/${sm.language}/story/${story.slugName}/",
          ZonedDateTime.of(story.modified, ZoneId.of("UTC")), null
        )
      )) ++ sitemap.collections.flatMap(sm => sm.collections.flatMap( collection =>
        Seq(SitemapXmlElement(
          s"$host/${sm.language}/collection/${collection.slugName}/",
          ZonedDateTime.of(collection.modified, ZoneId.of("UTC")), null
        )
        ) ++ collection.stories.map(story =>
          SitemapXmlElement(
            s"$host/${sm.language}/story/${collection.slugName}/$story/",
            ZonedDateTime.of(collection.modified, ZoneId.of("UTC")), null
          )
        )
      ))
      SitemapXml(elements)
    }
  }
}

class CraSitemapCreator extends SitemapCreator {

  /**
   *
   * @param host    is base host
   * @param sitemap is sitemap data
   * @return
   */
  override def create(host: String, sitemap: Sitemap): IO[SitemapXml] = {
    IO {
      val elements = sitemap.stories.flatMap(sm => sm.stories.map(story =>
        SitemapXmlElement(
          s"$host/story/${story.slugName}/",
          ZonedDateTime.of(story.modified, ZoneId.of("UTC")), null
        )
      ))  ++ sitemap.collections.flatMap(sm => sm.collections.map( collection =>
        SitemapXmlElement(
          s"$host/collection/${collection.slugName}/",
          ZonedDateTime.of(collection.modified, ZoneId.of("UTC")), null
        )
      ))
      SitemapXml(elements)
    }
  }
}