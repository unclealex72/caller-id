name := "callerid-local"

version := "2.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
    "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
    "com.sun.jersey" % "jersey-json" % "1.17.1",
    "com.sun.jersey" % "jersey-client" % "1.17.1",
    "com.sun.jersey" % "jersey-core" % "1.17.1",
    "com.sun.jersey.contribs" % "jersey-apache-client4" % "1.17.1",
    "com.google.api-client" % "google-api-client" % "1.10.2-beta",
    "uk.co.unclealex" % "process-support" % "2.0.2",
    "ch.qos.logback" % "logback-classic" % "1.0.6",
    "com.google.inject" % "guice" % "3.0",
    "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
    "uk.co.unclealex" % "test-server-utilities" % "1.0.1-SNAPSHOT" % "test")

resolvers ++= Seq(
  "cloudbees-private-release-repository" at "https://repository-unclealex.forge.cloudbees.com/release",
  "cloudbees-private-snapshot-repository" at "https://repository-unclealex.forge.cloudbees.com/snapshot")

