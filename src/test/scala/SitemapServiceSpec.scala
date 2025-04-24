package world.reveal.sitemap

import entities._
import models.{MockSitemapFileWriter, MockSitemapRepository}

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should.Matchers

import java.time.LocalDateTime
import java.util.UUID

class SitemapServiceSpec extends AsyncFreeSpec with AsyncIOSpec with Matchers{

  "SitemapService" - {
    val dbConfig = DbConfig(
      driver = "",
      url = "",
      username = "",
      password = "",
      maxPoolSize = 2
    )
    val awsConfig = AwsConfig(
      key = "",
      secret = "",
      region = "",
      bucketName = ""
    )
    val kafkaSettings = KafkaSettings(
      topic = "",
      group = "",
      server = ""
    )

    val authors = Map(
      "en" -> Seq(UUID.randomUUID(), UUID.randomUUID()),
      "ru" -> Seq(UUID.randomUUID(), UUID.randomUUID()),
    )
    val categories = Seq("Category name one", "Category name two")
    val englishStoryOne = StorySitemapData("english-story-one", LocalDateTime.now())
    val englishStoryTwo = StorySitemapData("english-story-two", LocalDateTime.now())
    val russianStoryOne = StorySitemapData("russian-story-one", LocalDateTime.now())
    val russianStoryTwo = StorySitemapData("russian-story-two", LocalDateTime.now())
    val stories = Map(
      "en" -> Seq(englishStoryOne,englishStoryTwo),
      "ru" -> Seq(russianStoryOne, russianStoryTwo)
    )
    val collections = Map(
      "en" -> Seq(
        SitemapCollectionData("english-collection-one", LocalDateTime.now(), List(englishStoryOne.slugName)),
        SitemapCollectionData("english-collection-two", LocalDateTime.now(), List(englishStoryTwo.slugName))
      ),
      "ru" -> Seq(
        SitemapCollectionData("russian-collection-one", LocalDateTime.now(), List(russianStoryOne.slugName)),
        SitemapCollectionData("russian-collection-two", LocalDateTime.now(), List(russianStoryTwo.slugName))
      )
    )
    val mockFileWriter = new MockSitemapFileWriter()
    val manager = SitemapManager(
      new MockSitemapRepository(
        authors,
        categories,
        stories,
        collections
      ),
      new NextSitemapCreator(),
      mockFileWriter
    )

    "New application sitemap" - {
      val config = ApplicationConfig(
        mode = ServiceVersion.Next,
        languages = Seq("en", "ru"),
        path = "/tmp/sitemap.xml",
        host = "https://somesite.com",
        db = dbConfig,
        aws = awsConfig,
        kafkaSettings = kafkaSettings
      )
      val sitemapReader = SitemapService.generateSitemap(config)

      "shows language data" in {
        sitemapReader.run(manager).map{ _ =>
          val sitemapOption = mockFileWriter.sitemap
          assert(sitemapOption.isDefined)
          val sitemap = sitemapOption.get
          assert(sitemap.elements.exists(el => el.loc == s"${config.host}/ru/" && el.changeFreq == SitemapElementChangeFreq.Weekly))
          assert(sitemap.elements.exists(el => el.loc == s"${config.host}/en/" && el.changeFreq == SitemapElementChangeFreq.Weekly))
        }
      }

      "shows authors data" in {
        sitemapReader.run(manager).map{_ =>
          val sitemapOption = mockFileWriter.sitemap
          assert(sitemapOption.isDefined)
          val sitemap = sitemapOption.get
          assert(sitemap.elements.count(_.loc.contains("/author/")) == 4)
          authors.foreach(langAuthor =>
            langAuthor._2.foreach(author =>
            assert(sitemap.elements.contains(SitemapXmlElement(s"${config.host}/${langAuthor._1}/author/$author/", null, null)))
          ))
        }
      }

      "shows category data" in {
        sitemapReader.run(manager).map{_ =>
          val sitemapOption = mockFileWriter.sitemap
          assert(sitemapOption.isDefined)
          val sitemap = sitemapOption.get
          assert(sitemap.elements.count(_.loc.contains("/category/")) == 4)
          assert(sitemap.elements.contains(SitemapXmlElement(s"${config.host}/en/category/category-name-one/", null, null)))
          assert(sitemap.elements.contains(SitemapXmlElement(s"${config.host}/ru/category/category-name-one/", null, null)))
          assert(sitemap.elements.contains(SitemapXmlElement(s"${config.host}/en/category/category-name-two/", null, null)))
          assert(sitemap.elements.contains(SitemapXmlElement(s"${config.host}/ru/category/category-name-two/", null, null)))
        }
      }

      "shows stories data" in {
        sitemapReader.run(manager).map{_ =>
          val sitemapOption = mockFileWriter.sitemap
          assert(sitemapOption.isDefined)
          val sitemap = sitemapOption.get
          assert(sitemap.elements.count(_.loc.contains("/story/")) == 8)
          assert(sitemap.elements.exists(sm => sm.loc == s"${config.host}/en/story/${englishStoryOne.slugName}/"))
          assert(sitemap.elements.exists(sm => sm.loc == s"${config.host}/en/story/${englishStoryTwo.slugName}/"))
          assert(sitemap.elements.exists(sm => sm.loc == s"${config.host}/ru/story/${russianStoryOne.slugName}/"))
          assert(sitemap.elements.exists(sm => sm.loc == s"${config.host}/ru/story/${russianStoryTwo.slugName}/"))
        }
      }

      "shows collections data" in {
        sitemapReader.run(manager).map{_ =>
          val sitemapOption = mockFileWriter.sitemap
          assert(sitemapOption.isDefined)
          val sitemap = sitemapOption.get
          assert(sitemap.elements.count(_.loc.contains("/collection/")) == 4)
          collections.foreach(langColl =>
            langColl._2.foreach { collection =>
              assert(sitemap.elements.exists(sm => sm.loc == s"${config.host}/${langColl._1}/collection/${collection.slugName}/"))
              collection.stories.foreach(story =>
                sitemap.elements.exists(sm => sm.loc == s"${config.host}/${langColl._1}/story/${collection.slugName}/$story/")
              )
            })
        }
      }

    }





  }
}
