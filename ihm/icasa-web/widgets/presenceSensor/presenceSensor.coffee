
define(["hubu", "DeviceWidgetContract", "log4javascript"], (hub, DeviceWidgetContract, log4javascript) ->

    console.log("presenceSensor module loaded !!!");

    class PresenceSensorWidget extends DeviceWidgetContract
        name : null
        logger : null

        constructor : (name) ->
            @name = name;
            @logger = log4javascript.getLogger("PresenceSensorWidget");
            @logger.removeAllAppenders();
            @logger.addAppender(new log4javascript.BrowserConsoleAppender());

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

        getBaseIconURL : () -> null;
          # keep it empty

        getCurrentIconURL : () -> null;
          # keep it empty

        manageDynamicIcon : () -> false;
          # keep it empty

        manageDevice : (deviceServices, deviceType) -> false;

        getStatusWindowTemplateURL : () -> null;

        getDecorators : () -> null;

        init : (deviceViewModel) -> null;

    instance = hub.createInstance(PresenceSensorWidget, {name : "presenceSensorWidget-1"});

    console.log("presenceSensor module end loaded !!!");

    return instance;
);
