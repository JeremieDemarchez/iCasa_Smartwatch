###*
#   Service Contract definition.
#  Used to know if the simulator web app is connected to a remote data model and which one.
#
# @author Thomas Leveque
###
define(['hubu', 'contracts/DataModelConnectionMgr'], (hub, DataModelConnectionMgr) ->
  return ConnectionWidget =
    class ConnectionWidget extends HUBU.AbstractComponent

      hub : null;
      gatewayUrl : null;
      buttonId : "connection-status-button";

      dataModelMgr : null;

      getComponentName: () ->
        return 'ConnectionWidget';

      configure: (theHub, config) =>
        @hub = theHub;
        @connected = false;
        if (config?.buttonId?) then @buttonId = config.buttonId;

        @hub.requireService({
          component: @,
          contract: DataModelConnectionMgr,
          field: "dataModelMgr"
        });

      setGatewayURL : (usedURL) =>
        @gatewayUrl = usedURL;

      updateButton : () =>
        connected = false;
        if (@dataModelMgr.isConnected()?)
          connected = @dataModelMgr.isConnected();

        buttonElt = $("#" + @buttonId);
        buttonElt.removeClass("btn-success btn-danger");
        if (connected)
          buttonElt.text("Connected");
          buttonElt.addClass("btn-success");
        else
          buttonElt.text("Not Connected");
          buttonElt.addClass("btn-danger");

      reconnect : () =>
        @dataModelMgr.reconnect();

      start : () =>
        $("#" + @buttonId).removeClass("hidden");
        #@updateButton();

      stop : () =>
        $("#" + @buttonId).addClass("hidden");

);