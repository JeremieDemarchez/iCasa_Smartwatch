###*
#  iCasa-Simulator Gateway Connection Manager Service Contract definition.
#  Used to know if the simulator web app is connected to a gateway and which one.
#
# @author Thomas Leveque
###
define(() ->
  class RemoteNotifMgr

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
    # Returns true if access from gateway to webapp works.
    # In other words, returne true if remote notifications are currently managed.
    ###
    isConnected : () ->
      # keep it empty

    getConnectionEventTopic: () ->
      return "/icasa/remote/notif/connection";

  return new RemoteNotifMgr();
);