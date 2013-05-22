# @author Thomas Leveque
define ["jquery", "atmosphere", "dataModels/ICasaDataModel", 'domReady'], ($, atmosphere, DataModel) ->

  serverUrl = $("#map").attr("gatewayURL").replace(/\/$/, "");

  socket = atmosphere
  transport = "sse"
  # workaround for SECURITY Exception on Chrome while using sse on localhost
  isChrome = navigator.userAgent.toLowerCase().indexOf('chrome') > -1;
  if (isChrome && (serverUrl.indexOf("localhost") > -1))
      transport = "long-polling";

  requestUrl = "#server#/atmosphere/event".replace(/#server#/, serverUrl)
  request =
    url: requestUrl
    contentType: "application/json"
    logLevel: "debug"
    shared: true
    transport: transport
    trackMessageLength: true
    enableXDR: true
    fallbackTransport: "long-polling"
    dropAtmosphereHeaders: true
    attachHeadersAsQueryString: true

  request.onOpen = (response) ->
    transport = response.transport
    console.log("Connection opened using " + transport)

  request.onReconnect = (request, response) ->
    socket.info "Reconnecting"

  locateModel = (collection, requiredId, customId)->
      returnedModel = undefined;
      returnedModel = collection.get(requiredId);
      if ((returnedModel == null || returnedModel == undefined ) && (customId != undefined))
        collection.each((internalModel) ->
          if (internalModel.get(customId) == requiredId)
            returnedModel = internalModel;
        );
      return returnedModel;

  request.onMessage = (response) ->
    json = "undefined"
    message = response.responseBody
    try
      json = $.parseJSON(message)
    catch error
      console.log "This doesn't look like a valid JSON: ", message.data
      return
    console.log "Received message :", json

    # manage zone events
    if (json.eventType == "zone-added")
      zone = locateModel(DataModel.collections.zones, json.zoneId, "zoneId");
      if ((zone == null)  || (zone == undefined))
        DataModel.collections.zones.add(json.zone);
    if (json.eventType == "zone-removed")
      zone = locateModel(DataModel.collections.zones, json.zoneId, "zoneId");
      if ((zone != null)  && (zone != undefined))
        DataModel.collections.zones.remove(zone);
    if ((json.eventType == "zone-resized") || (json.eventType == "zone-moved") || (json.eventType == "zone-variable-added") || (json.eventType == "zone-variable-removed") || (json.eventType == "zone-variable-updated"))
      zone = locateModel(DataModel.collections.zones, json.zoneId, "zoneId");
      if ((zone != null)  && (zone != undefined))
        zone.set(json.zone);

    # manage device type events
    if (json.eventType == "device-type-added")
      deviceType = locateModel(DataModel.collections.deviceTypes, json.deviceTypeId, "deviceTypeId");
      if ((deviceType == null)  || (deviceType == undefined))
        DataModel.collections.deviceTypes.add(json.deviceType);
    if (json.eventType == "device-type-removed")
      deviceType = locateModel(DataModel.collections.deviceTypes, json.deviceTypeId, "deviceTypeId");
      if ((deviceType != null)  && (deviceType != undefined))
        DataModel.collections.deviceTypes.remove(deviceType);

    # manage device events
    if (json.eventType == "device-added")
      device = locateModel(DataModel.collections.devices,json.deviceId, "deviceId");
      if ((device == null)  || (device == undefined))
        DataModel.collections.devices.add(json.device);
    if (json.eventType == "device-removed")
      device = locateModel(DataModel.collections.devices,json.deviceId, "deviceId");
      if ((device != null)  && (device != undefined))
        DataModel.collections.devices.remove(device);
    if ((json.eventType == "device-position-update") || (json.eventType == "device-property-added") || (json.eventType == "device-property-removed") || (json.eventType == "device-property-updated"))
      device = locateModel(DataModel.collections.devices,json.deviceId, "deviceId");
      if ((device != null)  && (device != undefined))
        device.set(json.device);

    # manage person type events
    if (json.eventType == "person-type-added")
      personType = locateModel(DataModel.collections.personTypes,json.personTypeId, "personTypeId");
      if ((personType == null)  || (personType == undefined))
        DataModel.collections.personTypes.add(json.personType);
    if (json.eventType == "person-type-removed")
      personType = locateModel(DataModel.collections.personTypes,json.personTypeId, "personTypeId");
      if ((personType != null)  && (personType != undefined))
        DataModel.collections.personTypes.remove(personType);

    # manage person events
    if (json.eventType == "person-added")
      person = locateModel(DataModel.collections.persons, json.personId, "personId");
      if ((person == null)  || (person == undefined))
        DataModel.collections.persons.add(json.person);
    if (json.eventType == "person-removed")
      person = locateModel(DataModel.collections.persons, json.personId, "personId");
      if ((person != null)  && (person != undefined))
        DataModel.collections.persons.remove(person);
    if (json.eventType == "person-position-update")
      person = locateModel(DataModel.collections.persons, json.personId, "personId");
      if ((person != null) && (person != undefined))
        person.set(json.person);

    # manage clock event
    if (json.eventType == "clock-modified")
      clock = DataModel.models.clock;
      clock.set(json.clock);

    # manage script events
    if ((json.eventType == "script-updated") || (json.eventType == "script-started") || (json.eventType == "script-stopped") || (json.eventType == "script-resumed") || (json.eventType == "script-paused"))
      script = locateModel(DataModel.collections.scripts, json.scriptId, "scriptId");
      if ((script != null) && (script != undefined))
        script.set(json.script);
    if (json.eventType == "script-added")
      script = locateModel(DataModel.collections.scripts, json.scriptId, "scriptId");
      if ((script != null) && (script != undefined))
        DataModel.collections.scripts.add(json.script);
    if (json.eventType == "script-removed")
      script = locateModel(DataModel.collections.scripts, json.scriptId, "scriptId");
      if ((script != null) && (script != undefined))
        DataModel.collections.scripts.remove(script);

  request.onClose = (response) ->
    return console.log "Connection closed"

  request.onError = (response) ->
    return console.log "Connection error"

  subSocket = socket.subscribe(request)
