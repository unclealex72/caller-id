import sbt._
import sbt.Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "callerid-play"
  val appVersion = "2.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.scalaz" %% "scalaz-core" % "7.0.0",
    "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
    "com.sun.jersey" % "jersey-json" % "1.17.1",
    "com.sun.jersey" % "jersey-client" % "1.17.1",
    "com.sun.jersey" % "jersey-core" % "1.17.1",
    "com.sun.jersey.contribs" % "jersey-apache-client4" % "1.17.1",
    "org.slf4j" % "slf4j-api" % "1.6.6",
    "com.google.api-client" % "google-api-client" % "1.10.2-beta",
    "uk.co.unclealex" % "process-support" % "2.0.2",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "org.springframework" % "spring-webmvc" % "3.2.2.RELEASE",
    "org.mozilla" % "rhino" % "1.7R4",
    "com.google.inject" % "guice" % "3.0",
    "com.tzavellas" % "sse-guice" % "0.7.1",
    "org.squeryl" % "squeryl_2.10.0-RC5" % "0.9.5-5",
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    jdbc,
    "org.pac4j" % "play-pac4j_scala2.10" % "1.1.0-SNAPSHOT",
    "org.pac4j" % "pac4j-oauth" % "1.4.1-SNAPSHOT",
    "org.pac4j" % "pac4j-http" % "1.4.1-SNAPSHOT",
    /** Test */
    "com.h2database" % "h2" % "1.3.171" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
    "uk.co.unclealex" % "test-server-utilities" % "1.0.1-SNAPSHOT" % "test")

  val main = play.Project(appName, appVersion, appDependencies).settings(defaultScalaSettings: _*).settings{
    scalaVersion := "2.10.1"
    resolvers ++= Seq(
      "Sonatype snapshots repository" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "cloudbees-private-release-repository" at "https://repository-unclealex.forge.cloudbees.com/release",
      "cloudbees-private-snapshot-repository" at "https://repository-unclealex.forge.cloudbees.com/snapshot")
  }
}
