resolvers += "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"

// Play framework
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")
addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

// Packaging plugins
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.3")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.0")
