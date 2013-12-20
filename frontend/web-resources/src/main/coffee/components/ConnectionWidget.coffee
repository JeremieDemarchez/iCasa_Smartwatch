###*
#   Service Contract definition.
#  Used to know if the simulator web app is connected to a remote data model and which one.
#
# @author Thomas Leveque
###
define(['hubu', 'contracts/GatewayConnectionMgr', 'i18n!locales/nls/locale'], (hub, GatewayConnectionMgr, locale) ->
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
          $('#' + @buttonId).on('click', @reconnect); #Reconnect event
          $('#' + @buttonId).on('hover', @changeButtonTitle);#Change title to connect/reconnect

        @hub.requireService({
          component: @,
          contract:  GatewayConnectionMgr,
          bind:      "bindGatewayConnectionMgr",
          unbind:    "unbindGatewayConnectionMgr"
        });


      changeButtonTitle:(e)=>
        button =  $("#" + @buttonId);
        if e.handleObj.type == "mouseover"
          button.removeClass("btn-success btn-danger btn-warning");
          button.addClass("btn-primary")
          if @gatewayConnectionMgr.isConnected()
            button.text(locale["Reconnect"]);
          else
            button.text(locale["Connect"]);
        else
          button.removeClass("btn-primary")
          @updateButton();#revert button original behaviour.

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
        if (modelConnected) #&& notifConnected
          buttonElt.text(locale["Connected"]);
          buttonElt.addClass("btn-success");
        #else if (modelConnected)
        #  buttonElt.text(locale["Connected.Without.Notifications"]);
        #  buttonElt.addClass("btn-warning");
        else
          buttonElt.text(locale["Not.Connected"]);
          buttonElt.addClass("btn-danger");

      reconnect: () =>
        @gatewayConnectionMgr.reconnect();


)
;