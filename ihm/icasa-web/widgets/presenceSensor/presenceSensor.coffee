
define(["require", "hubu", "contracts/DeviceWidgetContract", "log4javascript"], (require, hub, DeviceWidgetContract, log4javascript) ->

    console.log("presenceSensor module loaded !!!");

    class PresenceSensorWidget extends DeviceWidgetContract
        name : null
        logger : null
        iconURL : null;

        constructor : (name) ->
            @name = name;
            @logger = log4javascript.getLogger("PresenceSensorWidget");
            @logger.removeAllAppenders();
            @logger.addAppender(new log4javascript.BrowserConsoleAppender());
            @iconURL = require.toUrl("./movementDetector.png");

        start : -> @logger.info("PresenceSensorWidget starting...");

        stop : -> @logger.info("PresenceSensorWidget stoping...");

        configure : (theHub, config) ->
          if (config?.name?) then @name = config.name;
          @hub = theHub;
          @hub.provideService({
            component: this,
            contract: DeviceWidgetContract
          });

        getComponentName: -> return @name;

        getBaseIconURL : () -> return @iconURL;
          # keep it empty

        getCurrentIconURL : () -> null;
          # keep it empty

        manageDynamicIcon : () -> false;
          # keep it empty

        manageDevice : (device) ->
          return ((@type() == "iCasa.PresenceSensor") || @hasService("fr.liglab.adele.icasa.device.presence.PresenceSensor"));

        getStatusWindowTemplateURL : () -> null;

        getDecorators : () -> null;

        init : (deviceViewModel) -> null;

    instance = hub.createInstance(PresenceSensorWidget, {name : "presenceSensorWidget-1"});

    console.log("presenceSensor module end loaded !!!");

    return instance;
);
