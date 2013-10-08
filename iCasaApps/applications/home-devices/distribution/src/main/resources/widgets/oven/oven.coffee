
define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], (require, hub, DeviceWidgetContract, ko, log4javascript) ->

  deviceTypeName = "Oven";

  console.log(deviceTypeName + " module loaded !!!");

  class OvenWidget extends DeviceWidgetContract
    name : null
    logger : null
    iconURL : null;

    constructor : (name) ->
      @name = name;
      @logger = log4javascript.getLogger(deviceTypeName + "Widget");
      @logger.removeAllAppenders();
      @logger.addAppender(new log4javascript.BrowserConsoleAppender());
      @iconURL = require.toUrl("./oven-icon.png");

    start : -> @logger.info(deviceTypeName + "Widget starting...");

    stop : -> @logger.info(deviceTypeName + "Widget stoping...");

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
      return ((device.type() == "iCasa.Oven") || device.hasService("fr.liglab.adele.home.devices.kitchen.Oven"));

    getStatusWindowTemplateURL : () -> null;

    getDecorators : () -> null;

    propHasChanged : (device) ->
      null;

    init : (deviceViewModel) ->
      null;

  instance = hub.createInstance(OvenWidget, {name : deviceTypeName + "Widget-1"});

  console.log(deviceTypeName + " module end loaded !!!");

  return instance;
);
