package models

abstract class Library {
    var id: String
    var name: String
    var description: String
    var gatewayURL: String
    var imgFile: String
    var libs: String

    def toXML =
      <Map id="{id}" name="{name}" description="{description}" gatewayURL="{gatewayURL}" imgFile="{imgFile}" libs="{libs}"/>
}