// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// An assembly for the local project
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.8.8")

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.1.1")

// Deploy to a WAR file
addSbtPlugin("com.github.play2war" % "play2-war-plugin" % "0.9")
