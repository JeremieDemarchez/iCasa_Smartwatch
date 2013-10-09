import sbt._
import sbt.Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "appstore"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
       "mysql" % "mysql-connector-java" % "5.1.18",
        "securesocial" % "securesocial_2.9.1" % "2.0.8"
    )


    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      templatesImport += "models.values._",
        resolvers += Resolver.url("sbt-plugin-snapshots", url("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
        resolvers += Resolver.url("Secure Social", url("http://securesocial.ws/repository/releases"))(Resolver.ivyStylePatterns)
    )

}
