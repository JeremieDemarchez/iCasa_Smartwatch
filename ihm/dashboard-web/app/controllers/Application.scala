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
import models.Library
import utils.RichFile.enrichFile
import com.typesafe.config.ConfigFactory

object Application extends Controller {

 /*
   * Plugin management methods.
   */

  val PLUGINS_DIRECTORY: String = "plugins";

 /*
  * Widget management methods.
  */

  val WIDGETS_DIRECTORY: String = "widgets";

 /*
  * Library management methods.
  */

  val LIBS_DIRECTORY: String = "libs";
  
  val MAP_DIRECTORY: String = "maps";

  var libs = mutable.Map.empty[String, Library];

  val libsLock = new java.lang.Object;

  def loadLib(libId : String): Library = {
    var lib : Library = null;
    libsLock.synchronized {
      val libFile = new java.io.File(Play.application.getFile(LIBS_DIRECTORY), libId + ".xml");
      if (libFile.exists()) {
        if (libs.contains(libId)) {
          lib = libs(libId);
        } else {
          lib = new Library(libId);
          libs(libId) = lib;
        }

        // parse xml file
        val libRootNode = xml.XML.loadFile(libFile);
        lib.plugins.clear();
        for (pluginNode <- (libRootNode \\ "plugin")) {
          val plugin = fromXML(pluginNode);
          lib.plugins += plugin.id;
        }
        lib.widgets.clear();
        for (widgetNode <- (libRootNode \\ "widget")) {
          val widget = fromXML(widgetNode);
          lib.widgets += widget.id;
        }
      }
    }
    return lib;
  }


  /*
   * Map management methods.
   */

  def fromXML(node: scala.xml.Node): HouseMap =
        new HouseMap {
            var id = (node \ "@id").text
            var name = (node \ "@name").text
            var description = (node \ "@description").text
            var gatewayURL = (node \ "@gatewayURL").text
            var imgFile = (node \ "@imgFile").text
            var libs = (node \ "@libs").text
        }

  var maps = mutable.Map.empty[String, HouseMap];

  val mapsLock = new java.lang.Object;

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
       val mapsFile = new java.io.File(Play.application.getFile(MAP_DIRECTORY), "maps.xml");
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
    val mapsFile = new java.io.File(Play.application.getFile(MAP_DIRECTORY), "maps.xml");

    mapsLock.synchronized {
      if (mapsFile.exists())
        mapsFile.createNewFile();

      var xmlStr = "<?xml version='1.0' encoding='UTF-8'?>\n<maps>\n";
      for ((houseMapId, houseMap) <- maps) {
         xmlStr += "<map id=\"" + houseMap.id + "\" name=\"" + houseMap.name +
          "\" description=\"" + houseMap.description + "\" gatewayURL=\"" +
          houseMap.gatewayURL + "\" imgFile=\"" + houseMap.imgFile + "\" libs=\"" + houseMap.libs + "\"/>\n";
      }
      xmlStr += "</maps>";

      mapsFile.text = xmlStr;
    }
  }

  def index() = Action { implicit request =>
      loadMaps();
      Ok(views.html.index(getMaps()));
  }
  
  def connectToMap(mapId: String) = Action { implicit request =>
    loadMaps();
    val map = maps(mapId);
    if (map == null)
      NotFound("Map with id " + mapId + " not found.");
    else  {
      var widgetIds = "";
      var pluginIds = "";
      for (libId <- map.getLibIds()) {
        val lib = loadLib(libId);
        if (lib != null) {
          for (widgetId <- lib.widgets) {
            if (!widgetIds.isEmpty())
              widgetIds += ",";
            widgetIds += widgetId;
          }
          for (pluginId <- lib.plugins) {
            if (!pluginIds.isEmpty())
              pluginIds += ",";
            pluginIds += pluginId;
          }
        }
      }

      Ok(views.html.map(mapId, "/dashboard/maps/" + map.imgFile, map.gatewayURL, pluginIds, widgetIds
      )).withHeaders(
        "Access-Control-Allow-Origin" -> "*",
        "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
        "Access-Control-Expose-Headers" -> "X-Cache-Date, X-Atmosphere-tracking-id",
        "Access-Control-Allow-Headers" ->"Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport",
        "Access-Control-Max-Age"-> "-1"
      )
    }
  }


  val mapForm = Form(
      tuple(
        "mapId" -> text,
        "mapName" -> text,
        "mapDescription" -> text,
        "gatewayURL" -> text,
        "libs" -> text
      )
  )

  def uploadMap = Action(parse.multipartFormData) { implicit request =>
    val body = request.body;
    val (mapId, mapName, mapDescription, gatewayURLToSet, libsToSet) = mapForm.bindFromRequest.get;

    body.file("picture").map { picture =>
      import java.io.File
      val fileName = picture.filename
      val contentType = picture.contentType
      var newFileName = ""
      mapsLock.synchronized {
        val newId = generateMapId(mapName);
        newFileName = newId + ".png";
        def map = new HouseMap {
            var name = mapName;
            var id = newId;
            var description = mapDescription;
            var gatewayURL = gatewayURLToSet;
            var imgFile = newFileName;
            var libs = libsToSet;
        }
        if (maps.isEmpty)
          loadMaps();

        maps(map.id) = map;
        saveMaps();
      }
      picture.ref.moveTo(Play.getFile(MAP_DIRECTORY + "/" + newFileName))
      Redirect(routes.Application.index)
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
        "error" -> "Missing file"
      )
    }
  }

  def generateMapId(name: String):  String = {
    var mapId = name.replaceAll("[^A-Za-z0-9]", "");//remove non-alphanumeric char
      while (maps.contains(mapId)){
        mapId = mapId + scala.util.Random.alphanumeric.take(2).mkString; //get an alphanumeric character
      }
    return mapId;
  }

  def getMap(file: String) = Action {
    val fileToServe = new java.io.File(Play.application.getFile(MAP_DIRECTORY), file);
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

          def map = new HouseMap {
              var id = newMap("mapId");
              var name = newMap("mapName");
              var description = newMap("mapDescription");
              var gatewayURL = newMap("gatewayURL");
              var imgFile = id + ".png";
              var libs = newMap("libs");
          }
          picture.ref.moveTo(Play.getFile(MAP_DIRECTORY + "/" + map.imgFile), true)

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
                  var imgFile = oldMap.imgFile; //We do not modify the image
                  var libs = oldMap.libs //We do not modify libraries
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

  /**
   * Used to check version compatibility between gateways and web application.
   */
  def frontendInfo() = Action {  implicit request =>
    var version = "0.0.0";
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