name := """Caller ID"""

version := "2.0-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.3",
  "io.argonaut" %% "argonaut" % "6.1")

/* Dependency Injection */
libraryDependencies ++= Seq(/*"play" -> "0.5.8",*/ "akka" -> "0.5.6").map(kv => "org.scaldi" %% s"scaldi-${kv._1}" % kv._2)

/* Testing */
libraryDependencies ++= Seq("core", "mock", "matcher-extra", "analysis", "junit").map(
  suffix => "org.specs2" %% s"specs2-$suffix" % "3.6.4" % "test")

/* Logging */
libraryDependencies ++= Seq("com.typesafe.scala-logging" %% "scala-logging" % "3.1.0", "ch.qos.logback" % "logback-classic" % "1.1.3")

/* Squeezebox */
libraryDependencies += "com.google.http-client" % "google-http-client" % "1.20.0"

/* Akka */
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.12"

/* Testing */
libraryDependencies ++= Seq("core", "mock").map(suffix => "org.specs2" %% s"specs2-$suffix" % "3.6.4" % "test")

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

scalacOptions in Test ++= Seq("-Yrangepos")