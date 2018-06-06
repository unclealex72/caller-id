import sbt.Resolver

name := """callerid"""
organization := "uk.co.unclealex"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  ws,
  ehcache,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26",
  "com.mohiva" %% "play-silhouette" % "5.0.4",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.4",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.4",
  "org.webjars.npm" % "materialize-css" % "1.0.0-rc.1",
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
