###*
# .
#
# @author Thomas Leveque
###
define(['hubu', 'contracts/GatewayConnectionMgr', 'contracts/DataModelConnectionMgr', 'contracts/RemoteNotifMgr'], (hub, GatewayConnectionMgr, DataModelConnectionMgr, RemoteNotifMgr) ->
  return class GatewayConnectionMgrImpl extends HUBU.AbstractComponent

    hub: null;
    name: null;
    gatewayBaseUrl: null;
    modelConnected : false;
    notifsConnected : false;

    dataModelConnectionMgr: null;
    remoteNotifConnectionMgr: null;

    getComponentName: () ->
      return @name;

    start: () ->
      # do nothing

    stop: () ->
      # do nothing

    configure: (theHub, config) ->
      @hub = theHub;
      @modelConnected = false;
      @notifsConnected = false;
      if (config?.name?)
        @name = config.name;

      @hub.requireService({
        component: @,
        contract:  DataModelConnectionMgr,
        bind:      "bindDataModelConnectionMgr",
        unbind:    "unbindDataModelConnectionMgr"
      });
      @hub.requireService({
        component: @,
        contract:  RemoteNotifMgr,
        bind:      "bindRemoteNotifMgr",
        unbind:    "unbindRemoteNotifMgr"
      });
      @hub.provideService({
        component: @,
        contract: GatewayConnectionMgr
      });

    setModelConnected : (connectedFlag) =>
      if (@modelConnected != connectedFlag)
        if (connectedFlag)
            @remoteNotifConnectionMgr.reconnect()
        @modelConnected = connectedFlag;
        @hub.publish(@, @getConnectionEventTopic(), {"modelConnected" : connectedFlag, "notifsConnected" : @notifsConnected});

    setNotifsConnected : (connectedFlag) =>
      if (@notifsConnected != connectedFlag)
        @notifsConnected = connectedFlag;
        @hub.publish(@, @getConnectionEventTopic(), {"modelConnected" : @modelConnected, "notifsConnected" : connectedFlag});

    updateConnectedFlags : () ->
      # manage data model connection state
      newModelConnected = false;
      if (@dataModelConnectionMgr?)
        newModelConnected = @dataModelConnectionMgr.isConnected();
      @setModelConnected(newModelConnected);

      # manage notif connection state
      newNotifConnected = false;
      if (@remoteNotifConnectionMgr?)
        newNotifConnected = @remoteNotifConnectionMgr.isConnected();
      @setNotifsConnected(newNotifConnected);

    notifyModelConnectionEvent : (event) ->
      @updateConnectedFlags();

    notifyNotifConnectionEvent : (event) ->
      @updateConnectedFlags();

    bindDataModelConnectionMgr: (svc) ->
      @dataModelConnectionMgr = svc;
      @hub.subscribe(@, @dataModelConnectionMgr.getConnectionEventTopic(), @notifyModelConnectionEvent);
      @updateConnectedFlags();

    unbindDataModelConnectionMgr: (svc) ->
      @hub.unsubscribe(@, @notifyModelConnectionEvent);
      @modelConnected = false;
      @dataModelConnectionMgr = null;

    bindRemoteNotifMgr: (svc) ->
      @remoteNotifConnectionMgr = svc;
      @hub.subscribe(@, @remoteNotifConnectionMgr.getConnectionEventTopic(), @notifyNotifConnectionEvent);
      @updateConnectedFlags();

    unbindRemoteNotifMgr: (svc) ->
      @hub.unsubscribe(@, @notifyNotifConnectionEvent);
      @notifsConnected = false;
      @remoteNotifConnectionMgr = null;

    setGatewayBaseURL: (usedURL) ->
      @gatewayBaseUrl = usedURL;

    getGatewayBaseURL : () ->
      return @gatewayBaseUrl;

    getGatewayRemoteRootURL : () ->
      return @getGatewayBaseURL() + "/icasa";

    isConnected : () ->
      return @modelConnected;

    isRemoteNotifsManaged : () ->
      return @notifsConnected;

    getConnectionEventTopic: () ->
      return GatewayConnectionMgr.getConnectionEventTopic();

    reconnect: () ->
      @gatewayConnectionMgr.reconnect();
      @remoteNotifConnectionMgr.reconnect();


)
;