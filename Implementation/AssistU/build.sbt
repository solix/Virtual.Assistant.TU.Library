
name := "AssistU"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "commons-io" % "commons-io" % "2.4",
  "commons-lang" % "commons-lang" % "2.3",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "com.google.guava" % "guava" % "14.0",
  filters,
  "be.objectify"  %% "deadbolt-java"     % "2.3.0-RC1",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
//  "com.feth"      %% "play-authenticate" % "0.6.5-SNAPSHOT",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.webjars" % "bootstrap" % "3.2.0",
  "org.webjars" %% "webjars-play" % "2.3.0",
  "org.webjars" % "jquery-ui" % "1.11.0-1"
  )

resolvers ++= Seq(
  "Apache" at "http://repo1.maven.org/maven2/",
  "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
  "play-easymail (release)" at "http://joscha.github.io/play-easymail/repo/releases/",
  "play-easymail (snapshot)" at "http://joscha.github.io/play-easymail/repo/snapshots/",
  Resolver.url("Objectify Play Repository", url("http://schaloner.github.io/releases/"))(Resolver.ivyStylePatterns),
  "play-authenticate (release)" at "http://joscha.github.io/play-authenticate/repo/releases/",
  "play-authenticate (snapshot)" at "http://joscha.github.io/play-authenticate/repo/snapshots/"
)

