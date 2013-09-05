package controllers

import play.api._
import play.api.mvc._
import play.api.libs._
import play.api.libs.iteratee._

import Play.current

import java.io._

/**
 * Controller that serves static resources from an external folder.
 * It useful in development mode if you want to serve static assets that shouldn't be part of the build process.
 *
 * The default is to serve all assets with max-age=3600, this can be overwritten in application.conf file via
 *
 * {{{
 * assets.defaultCache = "no-cache"
 * }}}
 *
 * You can also set a custom Cache directive for a particular resource if needed
 *
 * For example in your application.conf file:
 *
 * {{{
 * assets.cache./public/images/logo.png = "max-age=5200"
 * }}}
 *
 * You can use this controller in any application, just by declaring the appropriate route. For example:
 * {{{
 * GET     /assets/\uFEFF*file               controllers.ExternalAssets.at(path="/home/peter/myplayapp/external", file)
 * GET     /assets/\uFEFF*file               controllers.ExternalAssets.at(path="C:\external", file)
 * GET     /assets/\uFEFF*file               controllers.ExternalAssets.at(path="relativeToYourApp", file)
 * }}}
 *
 */
object SecuredExternalAssets extends Controller {

  val AbsolutePath = """^(/|[a-zA-Z]:\\).*""".r

  /**
   * Generates an `Action` that serves a static resource from an external folder
   *
   * @param rootPath the root folder for searching the static resource files such as `"/home/peter/public"`, `C:\external` or `relativeToYourApp`
   * @param file the file part extracted from the URL
   */
  def at(rootPath: String, file: String): Action[AnyContent] = Action { request =>
    val defaultCache = Play.configuration.getString("assets.defaultCache").getOrElse("max-age=3600")

    val fileToServe = rootPath match {
      case AbsolutePath(_) => new File(rootPath, file)
      case _ => new File(Play.application.getFile(rootPath), file)
    }

    //TODO should avoid dotdot attacks

    if (fileToServe.exists) {
      Ok.sendFile(fileToServe, inline = true).withHeaders(CACHE_CONTROL -> defaultCache)
    } else {
      NotFound
    }

  }
}