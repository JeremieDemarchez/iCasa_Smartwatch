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
    fallbackTransport: "long-polling"

  request.onOpen = (response) ->
    transport = response.transport
    console.log "Connection opened"

  request.onReconnect = (request, response) ->
    socket.info "Reconnecting"

  request.onMessage = (response) ->
    json = undefined
    message = undefined
    message = response.responseBody
    try
      json = $.parseJSON(message)
    catch error
      console.log "This doesn't look like a valid JSON: ", message.data
      return
    console.log "Received message :", json

  request.onClose = (response) ->
    console.log "Connection closed"

  request.onError = (response) ->
    console.log "Connection error"

  subSocket = socket.subscribe(request)
  subSocket