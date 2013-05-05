import sbt._
import Keys._

object CallerIdBuild extends Build {
  lazy val root = Project(id = "callerid",
                          base = file(".")) aggregate(local, remote)

  lazy val local = Project(id = "callerid-local",
                           base = file("callerid-local"))
  lazy val remote = Project(id = "callerid-remote",
                            base = file("callerid-remote"))
}

