import sbt._
import Keys._
import play.Project._

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
      javaJpa
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
      // Add your own project settings here      
    )

}
