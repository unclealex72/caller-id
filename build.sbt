import com.typesafe.sbt.packager.docker._
import sbt.Keys._
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._

name := """caller-id"""
organization := "uk.co.unclealex"

lazy val root = (project in file(".")).enablePlugins(PlayScala, DockerPlugin, AshScriptPlugin)

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  ws,
  ehcache,
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.beachape" %% "enumeratum" % "1.5.13",
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.13.0-play26",
  "com.mohiva" %% "play-silhouette" % "5.0.4",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.4",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.4",
  "org.webjars.npm" % "materialize-css" % "1.0.0-rc.1",
  "org.webjars" % "jquery" % "3.3.1-1",
  "nl.martijndwars" % "web-push" % "3.1.0",
  "com.google.apis" % "google-api-services-people" % "v1-rev288-1.23.0"
)

libraryDependencies ++= Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2",
  "com.github.simplyscala" %% "scalatest-embedmongo" % "0.2.4"
).map(_ % Test)

// Docker

dockerBaseImage := "openjdk:alpine"
dockerExposedPorts := Seq(9000)
maintainer := "Alex Jones <alex.jones@unclealex.co.uk>"
dockerRepository := Some("unclealex72")
version in Docker := "latest"

// Releases

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies, // : ReleaseStep
  inquireVersions, // : ReleaseStep
  runTest, // : ReleaseStep
  setReleaseVersion, // : ReleaseStep
  commitReleaseVersion, // : ReleaseStep, performs the initial git checks
  tagRelease, // : ReleaseStep
  releaseStepCommand("docker:publish"), // : ReleaseStep, build server docker image.
  setNextVersion, // : ReleaseStep
  commitNextVersion, // : ReleaseStep
  pushChanges // : ReleaseStep, also checks that an upstream branch is properly configured
)
