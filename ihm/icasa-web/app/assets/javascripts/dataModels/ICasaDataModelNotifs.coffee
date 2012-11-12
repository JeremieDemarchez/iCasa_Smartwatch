# @author Thomas Leveque
define(['knockout',
        'knockback',
        'atmosphere',
        'dataModels/ICasaDataModel'],
        (ko, kb, atmosphere, DataModel) ->
            socket = atmosphere;
            subSocket;
            transport = 'sse';

            # We are now ready to cut the request
            serverUrl = "http://localhost:8080/icasa";
            request = { url: 'http://#server#/atmosphere/event'.replace /#server#/, serverUrl,
                        contentType : "application/json",
                        logLevel : 'debug',
                        shared : true,
                        transport : transport ,
                        trackMessageLength : true,
                        fallbackTransport: 'long-polling'
            };

            request.onOpen = (response) ->
                transport = response.transport;

            request.onReconnect = (request, response) ->
                socket.info("Reconnecting")

            request.onMessage = (response) ->
                message = response.responseBody;
                try
                   json = jQuery.parseJSON(message);
                catch error
                   console.log('This doesn\'t look like a valid JSON: ', message.data);
                   return;
                console.log('Received message :', json);

            request.onClose = (response) ->
                console.log('Connection closed');

            request.onError = (response) ->
                console.log('Connection error');

            subSocket = socket.subscribe(request);

            return subSocket;
);
