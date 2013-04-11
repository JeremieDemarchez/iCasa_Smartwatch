import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    def fromEnv(name: String) = System.getenv(name) match {
      case null => None
      case value => Some(value)
    }
    val appName = fromEnv("project.artifactId").getOrElse("icasa-web")
    val appVersion = fromEnv("project.version").getOrElse("1.1.0-SNAPSHOT")

    val appDependencies = Seq(

    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(

        // compilation of Cofeescript
        //coffeescriptOptions := Seq("bare")

    )

}
