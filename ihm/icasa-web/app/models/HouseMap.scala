package models

import collection.mutable.Set

abstract class HouseMap {
    var id: String
    var name: String
    var description: String
    var gatewayURL: String
    var imgFile: String
    var libs: String

    def getLibIds() : Set[String] = {
      var libIds = Set[String]();
      val libStrs = libs.split(",");
      for (libId <- libStrs) {
          libIds += libId.trim;
      }

      return libIds;
    }

    def toXML =
      <Map id="{id}" name="{name}" description="{description}" gatewayURL="{gatewayURL}" imgFile="{imgFile}" libs="{libs}"/>
}