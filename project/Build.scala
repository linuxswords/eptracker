import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "episode-play"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(

    // html access and parsing
      "org.jsoup" % "jsoup" % "1.6.2",

    // persistence layer
      "com.googlecode.mapperdao" % "mapperdao" % "1.0.0-rc9",
      "mysql" % "mysql-connector-java" % "5.1.18"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
