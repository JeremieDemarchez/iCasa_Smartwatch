
define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], (require, hub, DeviceWidgetContract, ko, log4javascript) ->

    console.log("door module loaded !!!");

    class DoorSensorWidget extends DeviceWidgetContract
        name : null
        logger : null
        iconURL : null;

        constructor : (name) ->
            @name = name;
            @logger = log4javascript.getLogger("DoorSensorWidget");
            @logger.removeAllAppenders();
            @logger.addAppender(new log4javascript.BrowserConsoleAppender());
            @iconURL = require.toUrl("./door_icon.jpg");

        start : -> @logger.info("DoorSensorWidget starting...");

        stop : -> @logger.info("DoorSensorWidget stoping...");

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
          return ((device.type() == "iCasa.Door") || device.hasService("fr.liglab.adele.home.devices.general.Door"));

        getStatusWindowTemplateURL : () -> null;

        getDecorators : () -> null;

        propHasChanged : (device) ->
          null;

        init : (deviceViewModel) ->
          null;

    instance = hub.createInstance(DoorSensorWidget, {name : "doorSensorWidget-1"});

    console.log("doorSensor module end loaded !!!");

    return instance;
);
