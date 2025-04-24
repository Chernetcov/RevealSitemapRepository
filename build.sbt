ThisBuild / version := "0.0.1"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "RevealSitemapService",
    idePackagePrefix := Some("world.reveal.sitemap")
  )

libraryDependencies ++= Seq(
  "com.github.fd4s" %% "fs2-kafka" % "3.7.0",
  "org.postgresql" % "postgresql" % "42.7.5",
  "org.typelevel" %% "cats-core" % "2.13.0",
  "org.tpolecat" %% "doobie-core" % "1.0.0-RC9",
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC9",
  "org.tpolecat" %% "doobie-hikari"    % "1.0.0-RC9",
  "com.zaxxer" % "HikariCP" % "6.3.0",
  "org.scalactic" %% "scalactic" % "3.2.19",
  "org.scalatest" %% "scalatest" % "3.2.19" % "test",
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.6.0" % Test,
  "com.github.pureconfig" %% "pureconfig" % "0.17.9",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "org.scala-lang.modules" %% "scala-xml" % "2.3.0",
  "software.amazon.awssdk" % "s3" % "2.31.28",
)