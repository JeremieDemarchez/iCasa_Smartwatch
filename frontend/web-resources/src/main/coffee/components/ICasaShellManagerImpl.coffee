###*
#   Service Contract definition.
#  Used to know if the simulator web app is connected to a remote data model and which one.
#
# @author Thomas Leveque
###
define(['jquery','hubu', 'contracts/ICasaShellManager'], ($, hub, contract) ->
  return class ICasaShellManagerImpl extends HUBU.AbstractComponent

      hub: null;
      name: null;
      #gatewayUrl: $("#map").attr("gatewayURL").replace(/\/$/, "");
      gatewayUrl: window.location.origin;
      shellResult: "shellOutput";
      historyCallNumber: 0;
      historyCalls: [];

      getComponentName: () ->
        return @name;

      start: () ->
        #

      stop: () ->
        #

      configure: (theHub, config) ->
        @hub = theHub;
        if (config?.outputId?)
          @shellResult = config.outputId;
        if (config?.url?)
          @gatewayURL = config.url;

        @hub.provideService({
          component: @,
          contract: contract
        });

      printCommandResult: (data,  textStatus, jqXHR) =>
        json_data = JSON.stringify(data);
        result = "Unknown";
        jqueryElement = $('#' + @shellResult);
        if jqXHR == "Not Found"
          result = "Command not found";
        if data.result?
          result = data.result
          result = result.replace(/\n/g,"<br>");
          result = result.replace(/\t/g,"&nbsp;&nbsp;&nbsp;&nbsp;")
        old = $('#' + @shellResult).html();
        newData = old + "<br><br>" + result;
        jqueryElement.html(newData);
        jqueryElement.scrollTop(jqueryElement[0].scrollHeight);


      exec:(name, params)=>
        json_params = JSON.stringify(params);
        $.ajax(
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          type: 'POST',
          url: @gatewayURL + "/icasa/shell/execute/#{name}",
          dataType: 'json',
          data :  "{parameters: #{json_params}}",
          success: @.printCommandResult,
          error: @.printCommandResult
        );
        @historyCalls[@historyCallNumber] = name + " " + params;
        @historyCallNumber  = @historyCallNumber + 1;



)
;