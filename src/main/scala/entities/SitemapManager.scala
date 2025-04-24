package world.reveal.sitemap
package entities

import models.SitemapRepository

case class SitemapManager(repository: SitemapRepository,
                          creator: SitemapCreator,
                          fileWriter: SitemapFileWriter)
