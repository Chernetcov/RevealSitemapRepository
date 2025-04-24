package world.reveal.sitemap
package entities

import java.time.LocalDateTime
import java.util.UUID

/**
 * Sitemap author data
 * @param language is language connected with authors
 * @param authors is authors that write stories on selected language
 */
case class SitemapAuthors(language: String, authors: Seq[UUID])

/**
 * Story sitemap description
 * @param slugName is slug name of author that will be used in link
 * @param modified is story last update date
 */
case class StorySitemapData(slugName: String, modified: LocalDateTime)

/**
 * Sitemap stories data for selected language
 * @param language is language on which stories was written
 * @param stories is list of stories connected with language
 */
case class SitemapStories(language: String, stories: Seq[StorySitemapData])

/**
 * Sitemap data for standalone collection
 * @param slugName is collection slug name
 * @param modified is collection lat update time
 * @param stories is stories data that included in collection
 */
case class SitemapCollectionData(slugName: String, modified: LocalDateTime, stories: List[String])

/**
 * Sitemap collections data for selected language
 * @param language is language on which collection stories was written
 * @param collections is list of collections connected with language
 */
case class SitemapCollections(language: String, collections: Seq[SitemapCollectionData])

/**
 * All necessary data for sitemap creations
 * @param languages is languages sitemap data
 * @param authors is authors sitemap data
 * @param categories is categories sitemap data
 * @param stories is stories sitemap data
 * @param collections is collections sitemap data
 */
case class Sitemap(languages: Seq[String],
                  authors: Seq[SitemapAuthors],
                  categories: Seq[String],
                  stories: Seq[SitemapStories],
                  collections: Seq[SitemapCollections]
                  )
