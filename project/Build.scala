import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "episode-play"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    "org.jsoup" % "jsoup" % "1.6.2"
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
