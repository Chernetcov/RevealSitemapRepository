package world.reveal.sitemap
package models

import entities.{SitemapCollectionData, StorySitemapData}

import cats.effect.IO

import java.util.UUID

/**
 * Mock sitemap repository. Should be used in tests
 * @param authors is authors function return value
 * @param categories is categories function return value
 * @param stories is stories function return value
 * @param collections is collections function return value
 */
class MockSitemapRepository(
                           authors: Map[String, Seq[UUID]],
                           categories: Seq[String],
                           stories: Map[String, Seq[StorySitemapData]],
                           collections: Map[String, Seq[SitemapCollectionData]]
                           ) extends SitemapRepository {

  /**
   * Get list of authors who write stories on selected language
   *
   * @param language is language connected with author
   * @return list of author uuid
   */
  override def authors(language: String): IO[Seq[UUID]] = IO.pure(
    authors(language)
  )

  /**
   * Get categories slug names
   *
   * @return list of categories slug names
   */
override def categories(): IO[Seq[String]] = IO.pure(categories)

  /**
   * Get stories sitemap data
   *
   * @param language is stories language
   * @return list of stories data
   */
override def stories(language: String): IO[Seq[StorySitemapData]] = IO.pure(stories(language))

  /**
   * Get collections sitemap data
   *
   * @param language is collections language
   * @return list of collections data
   */
  override def collections(language: String): IO[Seq[SitemapCollectionData]] = IO.pure(collections(language))
}
