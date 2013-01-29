package controllers

import play.api._
import libs.EventSource
import libs.json.Json._
import play.api.mvc._
import play.api.Play.current
import java.io.File
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable
import models.HouseMap

object Application extends Controller {

  def fromXML(node: scala.xml.Node): HouseMap =
        new HouseMap {
            var id = (node \ "@id").text
            var name = (node \ "@name").text
            var description = (node \ "@description").text
            var gatewayURL = (node \ "@gatewayURL").text
            var imgFile = (node \ "@imgFile").text
        }

  def loadMaps() = {
     val mapsFile = new File(Play.application.getFile(MAP_DIRECTORY), "maps.xml");
     if (mapsFile.exists()) {
         val mapsRootNode = xml.XML.loadFile(mapsFile);
         for (mapNode <- (mapsRootNode \\ "map")) {
            val map = fromXML(mapNode);
            maps(map.id) = map;
         }
     }
  }

  var maps = mutable.Map.empty[String, HouseMap];

  def getMaps(): Seq[HouseMap] = {
      var mapsSeq = mutable.Seq.empty[HouseMap]
      for ((mapId, map) <- maps) {
        //TODO use += instead (cannot use it for now due to a compilation error)
        mapsSeq = mapsSeq :+ map
      }

      return mapsSeq;
  }

  def index() = Action {
      loadMaps();
      Ok(views.html.index(getMaps()));
  }
  
  def connectToMap(mapId: String) = Action {

    Ok(views.html.map(mapId, "/maps/paulHouse.png", "http://localhost:8080")).withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Expose-Headers" -> "X-Cache-Date, X-Atmosphere-tracking-id",
      "Access-Control-Allow-Headers" ->"Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport",
      "Access-Control-Max-Age"-> "-1"
    )
  }

  val MAP_DIRECTORY: String = "maps";

  val mapForm = Form(
      tuple(
        "mapId" -> text,
        "mapName" -> text,
        "mapDescription" -> text,
        "gatewayURL" -> text
      )
  )

  def uploadMap = Action(parse.multipartFormData) { implicit request =>
    val body = request.body;
    val (mapId, mapName, mapDescription, gatewayURLToSet) = mapForm.bindFromRequest.get;

    body.file("picture").map { picture =>
      import java.io.File
      val fileName = picture.filename
      val contentType = picture.contentType
      picture.ref.moveTo(Play.getFile(MAP_DIRECTORY + "/" + fileName))

      def map = new HouseMap {
          var id = mapId;
          var name = mapName;
          var description = mapDescription;
          var gatewayURL = gatewayURLToSet;
          var imgFile = fileName;
      }
      maps(map.id) = map;
      Ok("Map created")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file"
      )
    }
  }

  def showMaps() = Action {
    Ok(views.html.maps());
  }

  def getGrid() = Action {
      Ok(views.html.grid());
    }

  def getMap(file: String) = Action {
    val fileToServe = new File(Play.application.getFile(MAP_DIRECTORY), file);
    val defaultCache = Play.configuration.getString("assets.defaultCache").getOrElse("max-age=3600")
    Ok.sendFile(fileToServe, inline = true).withHeaders(CACHE_CONTROL -> defaultCache);
  }
}