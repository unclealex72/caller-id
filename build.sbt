/** Project */
name := "callerid"

version in ThisBuild := "2.0.0-SNAPSHOT"

organization in ThisBuild := "uk.co.unclealex.callerid"

scalaVersion in ThisBuild := "2.10.1"

EclipseKeys.withSource in ThisBuild := true

/** Resolvers */
resolvers in ThisBuild ++= Seq(
  "cloudbees-private-release-repository"
    at "https://repository-unclealex.forge.cloudbees.com/release",
  "cloudbees-private-snapshot-repository"
    at "https://repository-unclealex.forge.cloudbees.com/snapshot"
)

