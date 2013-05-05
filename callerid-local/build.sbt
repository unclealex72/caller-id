/** Project */
name := "callerid-local"

libraryDependencies <<= scalaVersion { scala_version => Seq(
  "org.scalaz" %% "scalaz-core" % "7.0.0",
  "org.scala-lang" % "scala-library" % scala_version,
  "com.fasterxml.jackson.jaxrs" % "jackson-jaxrs-json-provider" % "2.2.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.2.0",
  "com.sun.jersey" % "jersey-json" % "1.17.1",
  "com.sun.jersey" % "jersey-client" % "1.17.1",
  "com.sun.jersey" % "jersey-core" % "1.17.1",
  "com.sun.jersey.contribs" % "jersey-apache-client4" % "1.17.1",
  "org.slf4j" % "slf4j-api" % "1.6.6",
  "com.google.api-client" % "google-api-client" % "1.10.2-beta",
  "uk.co.unclealex" % "process-support" % "2.0.1",
  /** Test */
  "org.scalamock" %% "scalamock-scalatest-support" % "3.0.1" % "test",
  "uk.co.unclealex" % "test-server-utilities" % "1.0.1-SNAPSHOT" % "test"
)}
