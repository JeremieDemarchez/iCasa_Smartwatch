package models

abstract class HouseMap {
    var id: String
    var name: String
    var description: String
    var gatewayURL: String
    var imgFile: String

    def toXML =
      <Map id="{id}" name="{name}" description="{description}" gatewayURL="{gatewayURL}" imgFile="{imgFile}"/>
}