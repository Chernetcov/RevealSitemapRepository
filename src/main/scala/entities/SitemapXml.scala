package world.reveal.sitemap
package entities

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import scala.xml.Elem

object SitemapElementChangeFreq extends Enumeration{
  type SitemapElementChangeFreq = Value
  val Never, Always, Hourly, Daily, Weekly, Monthly, Yearly = Value
}

case class SitemapXmlElement( loc: String,
                              lastmod: ZonedDateTime,
                              changeFreq: entities.SitemapElementChangeFreq.SitemapElementChangeFreq = SitemapElementChangeFreq.Never
                           ) {
  def toXml: Elem = <url>
      <loc>{loc}</loc>{if (lastmod != null) <lastmod>{DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(lastmod)}</lastmod>}{if (changeFreq != null) <changefreq>{changeFreq.toString.toLowerCase}</changefreq> }
    </url>
}

case class SitemapXml(elements: Seq[SitemapXmlElement]) {
  def toXml: Elem = <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
    {elements.map(_.toXml)}
    </urlset>
}
