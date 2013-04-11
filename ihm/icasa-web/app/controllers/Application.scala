package controllers

import play.api._
import libs.EventSource
import libs.json.Json
import libs.json.Json._
import play.api.mvc._
import play.api.Play.current
import java.io.File
import play.api.data._
import play.api.data.Forms._
import scala.collection.mutable
import models.HouseMap
import utils.RichFile.enrichFile
import com.typesafe.config.ConfigFactory

object Application extends Controller {

  def fromXML(node: scala.xml.Node): HouseMap =
        new HouseMap {
            var id = (node \ "@id").text
            var name = (node \ "@name").text
            var description = (node \ "@description").text
            var gatewayURL = (node \ "@gatewayURL").text
            var imgFile = (node \ "@imgFile").text
        }

  var maps = mutable.Map.empty[String, HouseMap];

  val mapsLock = new Object;

  def getMaps(): Seq[HouseMap] = {
    var mapsSeq = mutable.Seq.empty[HouseMap]
    for ((mapId, map) <- maps) {
      //TODO use += instead (cannot use it for now due to a compilation error)
      mapsSeq = mapsSeq :+ map
    }

    return mapsSeq;
  }

  def loadMaps() = {
     mapsLock.synchronized {
       var newMaps = mutable.Map.empty[String, HouseMap];
       val mapsFile = new File(Play.application.getFile(MAP_DIRECTORY), "maps.xml");
       if (mapsFile.exists()) {
           val mapsRootNode = xml.XML.loadFile(mapsFile);
           for (mapNode <- (mapsRootNode \\ "map")) {
              val map = fromXML(mapNode);
             newMaps(map.id) = map;
           }
       }
       maps = newMaps;
     }
  }

  def saveMaps() = {
    val mapsFile = new File(Play.application.getFile(MAP_DIRECTORY), "maps.xml");

    mapsLock.synchronized {
      if (mapsFile.exists())
        mapsFile.createNewFile();

      var xmlStr = "<?xml version='1.0' encoding='UTF-8'?>\n<maps>\n";
      for ((houseMapId, houseMap) <- maps) {
         xmlStr += "<map id=\"" + houseMap.id + "\" name=\"" + houseMap.name +
          "\" description=\"" + houseMap.description + "\" gatewayURL=\"" +
          houseMap.gatewayURL + "\" imgFile=\"" + houseMap.imgFile + "\"/>\n";
      }
      xmlStr += "</maps>";

      mapsFile.text = xmlStr;
    }
  }

  def index() = Action {
      loadMaps();
      Ok(views.html.index(getMaps()));
  }
  
  def connectToMap(mapId: String) = Action {
    loadMaps();
    val map = maps(mapId);
    if (map == null)
      NotFound("");
    else
      Ok(views.html.map(mapId, "/maps/" + map.imgFile, map.gatewayURL)).withHeaders(
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

      mapsLock.synchronized {
        if (maps.isEmpty)
          loadMaps();

        maps(map.id) = map;
        saveMaps();
      }
      Redirect(routes.Application.index)
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file"
      )
    }
  }

  def getMap(file: String) = Action {
    val fileToServe = new File(Play.application.getFile(MAP_DIRECTORY), file);
    val defaultCache = Play.configuration.getString("assets.defaultCache").getOrElse("max-age=3600")
    Ok.sendFile(fileToServe, inline = true).withHeaders(CACHE_CONTROL -> defaultCache);
  }

  def updateMap = Action (parse.multipartFormData) { implicit request =>
    val body = request.body;
    val newMap = mapForm.bindFromRequest.data;
    body.file("picture").map { picture =>
          import java.io.File
          val fileName = picture.filename
          val contentType = picture.contentType
          picture.ref.moveTo(Play.getFile(MAP_DIRECTORY + "/" + fileName))

          def map = new HouseMap {
              var id = newMap("mapId");
              var name = newMap("mapName");
              var description = newMap("mapDescription");
              var gatewayURL = newMap("gatewayURL");
              var imgFile = fileName;
          }

          mapsLock.synchronized {
            if (maps.isEmpty)
              loadMaps();

            maps(map.id) = map;
          }
        }.getOrElse {

            var oldMap = maps(newMap("mapId"));
            def map = new HouseMap {
                  var id = oldMap.id; //mapId is not modified
                  var name = newMap("mapName");
                  var description = newMap("mapDescription");
                  var gatewayURL = newMap("gatewayURL");
                  var imgFile = oldMap.imgFile; //We didn't modify the image
             }
             mapsLock.synchronized {
                maps(map.id) = map; //replace the map
             }
        }
    saveMaps();
    Redirect(routes.Application.index);

  }

  def deleteMap() = Action {  implicit request =>
    val map = mapForm.bindFromRequest.data;
    mapsLock.synchronized {
        maps.remove(map("mapId"));
    }
    saveMaps();
    Redirect(routes.Application.index);
  }

  def frontendInfo() = Action {  implicit request =>
    def version = "0.0.0";
    try {
      val configuration = ConfigFactory.load("version.conf");
      version = configuration.getString("app.version");
    } catch {
      case e: Exception =>
        e.printStackTrace();
        version = "0.0.0";
    }
    val jsonObject = Json.toJson( Map (
      "version" -> version)
    )
    Ok(jsonObject);
  }
}