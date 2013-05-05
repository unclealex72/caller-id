/** Project */
name := "callerid-remote"

libraryDependencies <<= scalaVersion { scala_version => Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.0",
  "org.scala-lang" % "scala-library" % scala_version,
  "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
  "com.sun.jersey" % "jersey-client" % "1.17.1",
  "com.sun.jersey" % "jersey-core" % "1.17.1",
  "org.slf4j" % "slf4j-api" % "1.6.6",
  "ch.qos.logback" % "logback-classic" % "1.0.6",
  "org.springframework" % "spring-webmvc" % "3.2.2.RELEASE",
  "org.mozilla" % "rhino" % "1.7R4",
  /** Test */
  "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
  "uk.co.unclealex" % "test-server-utilities" % "1.0.1-SNAPSHOT" % "test"
)}
