###*
#  iCasa-Simulator Gateway Connection Manager Service Contract definition.
#  Used to know if the simulator web app is connected to a gateway and which one.
#
# @author Thomas Leveque
###
define(() ->
  class GatewayConnectionMgr

    ###*
    # Returns the base url of the gateway.
    # Returned URL does not contain trailing slash.
    # e.g. http://localhost:8080
    ###
    getGatewayBaseURL : () ->
      # keep it empty

    ###*
    # Returns the root url of the gateway remote access.
    # Returned URL does not contain trailing slash.
    # e.g. http://localhost:8080/icasa
    ###
    getGatewayRemoteRootURL : () ->
      # keep it empty

    ###*
    # Returns true if access from web app to gateway works.
    ###
    isConnected : () ->
      # keep it empty

    ###*
    # Try to reconnect the web app to the gateway to retrieve data model.
    ###
    reconnect : () ->
      # keep it empty

    ###*
    # Returns true if remote notifications are currently managed.
    # In other words, that communication from gateway to web application works.
    ###
    isRemoteNotifsManaged : () ->
     # keep it empty

    getConnectionEventTopic: () ->
      return "/icasa/gateway/connection";

  return new GatewayConnectionMgr();
);