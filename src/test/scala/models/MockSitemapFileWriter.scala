package world.reveal.sitemap
package models

import entities.SitemapXml

import cats.effect.IO

class MockSitemapFileWriter extends SitemapFileWriter {

  var sitemap: Option[SitemapXml] = None
  /**
   * Write file
   *
   * @param sitemap is sitemap data to save
   * @param path    is path to save sitemap
   * @return
   */
  override def write(sitemap: SitemapXml, path: String): IO[Unit] = {
    this.sitemap = Some(sitemap)
    IO.pure()
  }
}
