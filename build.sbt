name := """callerid"""
organization := "uk.co.unclealex"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  ws,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26",
  "com.gu" %% "play-googleauth" % "0.7.6-SNAPSHOT",
  "org.webjars" % "Semantic-UI" % "2.3.1",
  "org.webjars" % "jquery" % "3.3.1-1",
  "com.google.apis" % "google-api-services-people" % "v1-rev288-1.23.0"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4"
).map(_ % Test)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "uk.co.unclealex.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "uk.co.unclealex.binders._"
