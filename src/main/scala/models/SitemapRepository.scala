package world.reveal.sitemap
package models

import entities.{SitemapCollectionData, StorySitemapData}

import cats.effect.IO

import java.util.UUID

/**
 * Loads sitemap data
 */
trait SitemapRepository {

  /**
   * Get list of authors who write stories on selected language
   * @param language is language connected with author
   * @return list of author uuid
   */
  def authors(language: String): IO[Seq[UUID]]

  /**
   * Get categories slug names
   * @return list of categories slug names
   */
  def categories(): IO[Seq[String]]

  /**
   * Get stories sitemap data
   * @param language is stories language
   * @return list of stories data
   */
  def stories(language: String): IO[Seq[StorySitemapData]]

  /**
   * Get collections sitemap data
   * @param language is collections language
   * @return list of collections data
   */
  def collections(language: String): IO[Seq[SitemapCollectionData]]
}
