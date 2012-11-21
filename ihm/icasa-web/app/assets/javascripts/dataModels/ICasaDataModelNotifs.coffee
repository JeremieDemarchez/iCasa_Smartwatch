# @author Thomas Leveque
define ["jquery", "knockout", "knockback", "atmosphere", "dataModels/ICasaDataModel"], ($, ko, kb, atmosphere, DataModel) ->
  socket = atmosphere
  transport = "sse"
  serverUrl = "http://localhost:8080"
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
    if ((json.eventType == "device-type-added") || (json.eventType == "device-type-removed"))
      DataModel.collections.deviceTypes.fetch();
    if (json.eventType == "device-position-update")
      device = DataModel.collections.devices.get(json.deviceId);
      if ((device != null)  && (device != undefined))
        device.fetch();
      else
        DataModel.collections.devices.fetch();

    if ((json.eventType == "user-added") || (json.eventType == "user-removed"))
      DataModel.collections.persons.fetch();
    if (json.eventType == "user-position-update")
      person = DataModel.collections.persons.get(json.userId);
      if ((person != null) && (person != undefined))
        person.fetch();
      else
        DataModel.collections.persons.fetch();

  request.onClose = (response) ->
    console.log "Connection closed"

  request.onError = (response) ->
    console.log "Connection error"

  subSocket = socket.subscribe(request)
