
define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], (require, hub, DeviceWidgetContract, ko, log4javascript) ->

    console.log("faucet module loaded !!!");

    class FaucetWidget extends DeviceWidgetContract
        name : null
        logger : null
        iconURL : null;

        constructor : (name) ->
            @name = name;
            @logger = log4javascript.getLogger("FaucetWidget");
            @logger.removeAllAppenders();
            @logger.addAppender(new log4javascript.BrowserConsoleAppender());
            @iconURL = require.toUrl("./faucet.png");

        start : -> @logger.info("FaucetWidget starting...");

        stop : -> @logger.info("FaucetWidget stoping...");

        configure : (theHub, config) ->
          if (config?.name?) then @name = config.name;
          @hub = theHub;
          @hub.provideService({
            component: this,
            contract: DeviceWidgetContract
          });

        getComponentName: -> return @name;

        getBaseIconURL : () -> return @iconURL;

        getCurrentIconURL : () -> null;

        manageDynamicIcon : () -> false;

        manageDevice : (device) ->
          return ((device.type() == "iCasa.Faucet") || device.hasService("fr.liglab.adele.home.devices.water.Faucet"));

        getStatusWindowTemplateURL : () -> null;

        getDecorators : () -> null;

        propHasChanged : (device) ->
          null;

        init : (deviceViewModel) ->
          null;

    instance = hub.createInstance(FaucetWidget, {name : "faucetWidget-1"});

    console.log("faucet module end loaded !!!");

    return instance;
);
