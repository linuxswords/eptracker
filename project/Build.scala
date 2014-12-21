import sbt._
import Keys._
import play.Play.autoImport._
import PlayKeys._

object ApplicationBuild extends Build {

    val appName         = "episode-play"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(

    // html access and parsing
      "org.jsoup" % "jsoup" % "1.6.2",

    // persistence layer
      "com.googlecode.mapperdao" % "mapperdao" % "1.0.0.rc19-2.10.1",
      "mysql" % "mysql-connector-java" % "5.1.18",
      jdbc,
      cache, javaJpa


    )

    val main = Project(appName, file(".")).enablePlugins(play.PlayScala).settings(
      version := appVersion,
      libraryDependencies ++= appDependencies,
      requireJs += "main.js"
    )

}
