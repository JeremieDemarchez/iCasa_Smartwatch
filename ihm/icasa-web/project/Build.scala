import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "icasa-web"
    val appVersion      = "1.0.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(

        // compilation of Cofeescript
        //coffeescriptOptions := Seq("bare")

        // configuration of db mapping technology
        ebeanEnabled := true
    )

}
