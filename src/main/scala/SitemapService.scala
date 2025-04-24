package world.reveal.sitemap

import entities._

import cats.Traverse
import cats.data.Reader
import cats.effect.IO
import cats.syntax.apply._


object SitemapService {

  private def generateSlug(text: String): String = {
    text.toLowerCase().replace(" ", "-")
  }

  private def getSitemap(languages: Seq[String]): Reader[SitemapManager, IO[Sitemap]] =
    Reader[SitemapManager, IO[Sitemap]] { manager =>

    val seqTraverse = Traverse[Seq]

    val authorsIO = seqTraverse.traverse(languages)( language =>
        manager.repository.authors(language)
          .map(authors => SitemapAuthors(language, authors))
      )

    val categoriesIO = manager.repository.categories()
      .map(_.map(SitemapService.generateSlug))

    val storiesIO = seqTraverse.traverse(languages)( language =>
        manager.repository.stories(language)
          .map(stories => SitemapStories(language, stories))
      )

    val collectionsIO = seqTraverse.traverse(languages)( language =>
        manager.repository.collections(language)
          .map(collections => SitemapCollections(language, collections))
      )

    (
      authorsIO,
      categoriesIO,
      storiesIO,
      collectionsIO
    ).mapN((authors, categories, stories, collections) =>
      Sitemap(languages, authors, categories, stories, collections)
    )
  }

  private def create(host: String, sitemap: IO[Sitemap]): Reader[SitemapManager, IO[SitemapXml]] =
    Reader[SitemapManager, IO[SitemapXml]]{ manager =>
      sitemap.flatMap(sitemap =>
        manager.creator.create(host, sitemap)
      )
  }

  private def write(sitemap: IO[SitemapXml], path: String): Reader[SitemapManager, IO[Unit]] = Reader[SitemapManager, IO[Unit]] {
    manager =>
      sitemap.flatMap(sitemap =>
        manager.fileWriter.write(sitemap, path)
      )
  }

  def generateSitemap(config: ApplicationConfig): Reader[SitemapManager, IO[Unit]] = {
      for {
        sitemap <- getSitemap(config.languages)
        sitemapXml <- create(config.host, sitemap)
        response <- write(sitemapXml, config.path)
      } yield response
  }

}
