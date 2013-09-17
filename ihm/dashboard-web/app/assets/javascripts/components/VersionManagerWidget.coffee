###*
#   Service Contract definition.
#  Used to hide/show a div based on the connection status and in the version compatibility
#
# @author Issac Garcia
###
define(['hubu', 'contracts/GatewayConnectionMgr', 'contracts/ICasaManager'], (hub, GatewayConnectionMgr, ICasaManager) ->
  return class VersionManagerWidget extends HUBU.AbstractComponent

      hub: null;
      name: null;
      gatewayUrl: null;
      elementId: "compatibilityWarn";
      connected : false;
      gatewayConnectionMgr: null;
      iCasaMgr: null;

      getComponentName: () ->
        return @name;

      start: () ->
        @updateElement();

      stop: () ->
        $('#' + @elementId).addClass("hidden");

      configure: (theHub, config) ->
        @hub = theHub;
        @connected = false;
        if (config?.elementId?)
          @elementId = config.elementId;

        @hub.requireService({
          component: @,
          contract:  GatewayConnectionMgr,
          bind:      "bindGatewayConnectionMgr",
          unbind:    "unbindGatewayConnectionMgr"
        });

        @hub.requireService({
          component: @,
          contract:  ICasaManager,
          bind:      "bindICasaMgr",
          unbind:    "unbindICasaMgr"
        });


      notifyConnectionEvent : (event) ->
        @updateElement();

      bindGatewayConnectionMgr: (svc) ->
        @gatewayConnectionMgr = svc;
        @hub.subscribe(@, @gatewayConnectionMgr.getConnectionEventTopic(), @notifyConnectionEvent);
        @updateElement();

      unbindGatewayConnectionMgr: (svc) ->
        @hub.unsubscribe(@, @notifyConnectionEvent);
        @connected = false;
        @gatewayConnectionMgr = null;

      #called when the ICasaManager appear.
      bindICasaMgr: (svc) ->
        @iCasaMgr = svc;
        @updateElement();

      #called when the ICasaManager disapears
      unbindICasaMgr: (svc) ->
        @iCasaMgr = null;

      #to hide/show a div based on the connection status and in the version compatibility
      updateElement: () ->
        modelConnected = false;
        sameVersion = false;
        if (@gatewayConnectionMgr?)
          modelConnected = @gatewayConnectionMgr.isConnected();

        if (@iCasaMgr?)
          sameVersion = @iCasaMgr.getFrontendVersion() == @iCasaMgr.getBackendVersion();

        elementElt = $('#' + @elementId);
         #show warn only when is connected
        if (modelConnected && !sameVersion)
            elementElt.removeClass("hidden");
        else
            elementElt.addClass("hidden");
)
;