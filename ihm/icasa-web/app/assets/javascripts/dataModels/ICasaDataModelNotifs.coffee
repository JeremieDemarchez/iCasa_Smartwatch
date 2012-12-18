# @author Thomas Leveque
define ["jquery", "knockout", "knockback", "atmosphere", "dataModels/ICasaDataModel"], ($, ko, kb, atmosphere, DataModel) ->
  socket = atmosphere
  serverUrl = "http://" + window.location.hostname + ":8080"
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

  request.onMessage = (response) ->
    json = "undefined"
    message = response.responseBody
    try
      json = $.parseJSON(message)
    catch error
      console.log "This doesn't look like a valid JSON: ", message.data
      return
    console.log "Received message :", json
    if ((json.eventType == "zone-added") || (json.eventType == "zone-removed"))
      DataModel.collections.zones.fetch();

    if ((json.eventType == "device-type-added") || (json.eventType == "device-type-removed"))
      DataModel.collections.deviceTypes.fetch();
    if (json.eventType == "device-added")
      device = DataModel.collections.devices.get(json.deviceId);
      if ((device == null)  || (device == undefined))
        device = new DataModel.Models.Device({ id: json.deviceId });
        device.fetch();
        DataModel.collections.devices.push(device);
    if (json.eventType == "device-removed")
      device = DataModel.collections.devices.get(json.deviceId);
      if ((device != null)  && (device != undefined))
        DataModel.collections.devices.remove(device);
    if (json.eventType == "device-position-update")
      device = DataModel.collections.devices.get(json.deviceId);
      if ((device != null)  && (device != undefined))
        device.fetch();
      else
        DataModel.collections.devices.fetch();

    if ((json.eventType == "person-type-added") || (json.eventType == "person-type-removed"))
      DataModel.collections.personTypes.fetch();

    if ((json.eventType == "person-added") || (json.eventType == "person-removed"))
      DataModel.collections.persons.fetch();
    if (json.eventType == "person-position-update")
      person = DataModel.collections.persons.get(json.personId);
      if ((person != null) && (person != undefined))
        person.fetch();
      else
        DataModel.collections.persons.fetch();

  request.onClose = (response) ->
    console.log "Connection closed"

  request.onError = (response) ->
    console.log "Connection error"

  subSocket = socket.subscribe(request)
