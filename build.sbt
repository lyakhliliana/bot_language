gitimport Dependencies.*

ThisBuild / scalaVersion := "2.13.12"
ThisBuild / version := "0.1.0-SNAPSHOT"

Compile / compile / scalacOptions ++= Seq(
//  "-Werror",
  "-Wdead-code",
  "-Wextra-implicit",
  "-Wnumeric-widen",
  "-Wunused",
  "-Wvalue-discard",
  "-Xlint",
  "-Xlint:-byname-implicit",
  "-Xlint:-implicit-recursion",
  "-unchecked"
)
lazy val root = (project in file("."))
  .settings(
    name := "language-bot",
    libraryDependencies ++= Seq(
      catsEffect,
      catsCore,
      catsFree,
      sttpCirce,
      sttpCore,
      sttpOkHttp,
      asyncHttpClientBackendCats,
      scalaTest % Test,
      telegramAkka,
      telegramCore,
      translator,
      scalajHttp,
      doobieCore,
      doobieHikari,
      newType,
      pureConfig,
      sqlite
    )
  )
