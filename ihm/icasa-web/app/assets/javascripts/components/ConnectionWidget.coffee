###*
#   Service Contract definition.
#  Used to know if the simulator web app is connected to a remote data model and which one.
#
# @author Thomas Leveque
###
define(['hubu', 'contracts/GatewayConnectionMgr'], (hub, GatewayConnectionMgr) ->
  return class ConnectionWidget extends HUBU.AbstractComponent

      hub: null;
      name: null;
      gatewayUrl: null;
      buttonId: "connection-status-button";
      connected : false;
      gatewayConnectionMgr: null;

      getComponentName: () ->
        return @name;

      start: () ->
        $("#" + @buttonId).removeClass("hidden");

      stop: () ->
        $("#" + @buttonId).addClass("hidden");

      configure: (theHub, config) ->
        @hub = theHub;
        @connected = false;
        if (config?.buttonId?)
          @buttonId = config.buttonId;

        @hub.requireService({
          component: @,
          contract:  GatewayConnectionMgr,
          bind:      "bindGatewayConnectionMgr",
          unbind:    "unbindGatewayConnectionMgr"
        });

      notifyConnectionEvent : (event) ->
        @updateButton();

      bindGatewayConnectionMgr: (svc) ->
        @gatewayConnectionMgr = svc;
        @hub.subscribe(@, @gatewayConnectionMgr.getConnectionEventTopic(), @notifyConnectionEvent);
        @updateButton();

      unbindGatewayConnectionMgr: (svc) ->
        @hub.unsubscribe(@, @notifyConnectionEvent);
        @connected = false;
        @gatewayConnectionMgr = null;

      setGatewayURL: (usedURL) ->
        @gatewayUrl = usedURL;

      updateButton: () ->
        modelConnected = false;
        if (@gatewayConnectionMgr?)
          modelConnected = @gatewayConnectionMgr.isConnected();

        notifConnected = false;
        if (@gatewayConnectionMgr?)
          notifConnected = @gatewayConnectionMgr.isRemoteNotifsManaged();

        buttonElt = $("#" + @buttonId);
        buttonElt.removeClass("btn-success btn-danger btn-warning");
        if (modelConnected && notifConnected)
          buttonElt.text("Connected");
          buttonElt.addClass("btn-success");
        else if (modelConnected)
          buttonElt.text("Connected Without Notifications");
          buttonElt.addClass("btn-warning");
        else
          buttonElt.text("Not Connected");
          buttonElt.addClass("btn-danger");

      reconnect: () ->
        @gatewayConnectionMgr.reconnect();


)
;