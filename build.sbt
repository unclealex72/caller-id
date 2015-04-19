name := """Caller ID"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

/* DAO */
libraryDependencies += "org.squeryl" %% "squeryl" % "0.9.5-7"

/* Dependency Injection */
libraryDependencies ++= Seq("", "-play", "-akka").map(suffix => "org.scaldi" %% s"scaldi${suffix}" % "0.5.4")

// Authentication
libraryDependencies += "ws.securesocial" %% "securesocial" % "3.0-M3"

/* Testing */
libraryDependencies ++= Seq("core", "mock", "matcher-extra", "analysis", "junit").map(suffix => "org.specs2" %% s"specs2-${suffix}" % "3.5" % "test")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")