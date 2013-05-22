import sbt._
import sbt.Keys._
import play.Project._
import sbtassembly.Plugin._

object ApplicationBuild extends Build {

  import AssemblyKeys._

  def prefix = (name : String) => "callerid-" + name
  val v = "2.0-SNAPSHOT"
  val organisation = "uk.co.unclealex.callerid"
  val scala_version = "2.10.1"
  val commonResolvers = Seq(
    "cloudbees-private-release-repository" at "https://repository-unclealex.forge.cloudbees.com/release",
    "cloudbees-private-snapshot-repository" at "https://repository-unclealex.forge.cloudbees.com/snapshot")
  
  lazy val localProject = Project(prefix("local"), file("local")).
    settings(assemblySettings: _*).
    settings(
      version := v,
      organization := organisation,
      scalaVersion := scala_version,
      testOptions in Test += Tests.Argument("junitxml", """directory="test-reports""""),
      testListeners <<= (target, streams).map((t, s) => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath, s.log))),
      testResultReporter <<= testResultReporterTask,
      testResultReporterReset <<= testResultReporterResetTask,
      libraryDependencies ++= Seq(
        "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
        "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
        "com.sun.jersey" % "jersey-json" % "1.17.1",
        "com.sun.jersey" % "jersey-client" % "1.17.1",
        "com.sun.jersey" % "jersey-core" % "1.17.1",
        "com.sun.jersey.contribs" % "jersey-apache-client4" % "1.17.1",
        "com.google.api-client" % "google-api-client" % "1.10.2-beta",
        "ch.qos.logback" % "logback-classic" % "1.0.6",
        "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
        "com.google.inject" % "guice" % "3.0",
        /* Test */
        "play" %% "play-test" % play.core.PlayVersion.current % "test",
        "org.scalamock" %% "scalamock-specs2-support" % "3.0.1" % "test",
        "org.eclipse.jetty.aggregate" % "jetty-servlet" % "8.1.0.v20120127" % "test"),
      parallelExecution in Test := false,
      testOptions in Test += Tests.Setup { loader =>
        loader.loadClass("play.api.Logger").getMethod("init", classOf[java.io.File]).invoke(null, new java.io.File("."))
      },
      testOptions in Test += Tests.Cleanup { loader =>
        loader.loadClass("play.api.Logger").getMethod("shutdown").invoke(null)
      },
      testOptions in Test += Tests.Argument("sequential", "true"),
      testOptions in Test += Tests.Argument("-ujunitxml=console"),
      //testOptions in Test += Tests.Argument("junitxml", "console"),
      testListeners <<= (target, streams).map((t, s) => Seq(new eu.henkelmann.sbt.JUnitXmlTestsListener(t.getAbsolutePath, s.log))),
      testResultReporter <<= testResultReporterTask,
      testResultReporterReset <<= testResultReporterResetTask,
      resolvers ++= commonResolvers)

  lazy val playProject  = play.Project(prefix("play"), v).
    aggregate(localProject).
    settings(defaultScalaSettings: _*).
    settings (
      organization := organisation,
      libraryDependencies ++= Seq(
    		"org.scalaz" %% "scalaz-core" % "7.0.0",
    		"com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
    		"com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
    		"com.sun.jersey" % "jersey-json" % "1.17.1",
    		"com.sun.jersey" % "jersey-client" % "1.17.1",
    		"com.sun.jersey" % "jersey-core" % "1.17.1",
    		"com.sun.jersey.contribs" % "jersey-apache-client4" % "1.17.1",
    		"com.google.api-client" % "google-api-client" % "1.10.2-beta",
    		"uk.co.unclealex" % "process-support" % "2.0.2",
    		"ch.qos.logback" % "logback-classic" % "1.0.6",
    		"com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
        "org.springframework" % "spring-webmvc" % "3.2.2.RELEASE",
    		"org.mozilla" % "rhino" % "1.7R4",
    		"com.google.inject" % "guice" % "3.0",
    		"com.tzavellas" % "sse-guice" % "0.7.1",
    		"org.squeryl" % "squeryl_2.10.0-RC5" % "0.9.5-5",
    		"postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    		jdbc,
        /* Javascript */
        "org.webjars" % "bootstrap" % "2.3.1-1",
        "org.webjars" % "jquery" % "1.9.1",
        "org.webjars" % "webjars-play" % "2.1.0-1",
        "org.webjars" % "font-awesome" % "3.1.1-1",
    		/** Test */
    		"com.h2database" % "h2" % "1.3.171" % "test",
    		"org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
    		"uk.co.unclealex" % "test-server-utilities" % "1.0.1-SNAPSHOT" % "test"),
      scalaVersion := scala_version,
      resolvers ++= commonResolvers)
}
