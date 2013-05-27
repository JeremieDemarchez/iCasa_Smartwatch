###*
#  iCasa-Simulator Data Model Connection Manager Service Contract definition.
#  Used to know if the simulator web app is connected to a remote data model and which one.
#
# @author Thomas Leveque
###
define(() ->
  return DataModelConnectionMgr =
    class DataModelConnectionMgr

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
);