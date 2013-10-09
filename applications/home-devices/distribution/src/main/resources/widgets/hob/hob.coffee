
define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], (require, hub, DeviceWidgetContract, ko, log4javascript) ->

  deviceTypeName = "Hob";

  console.log(deviceTypeName + " module loaded !!!");

  class HobWidget extends DeviceWidgetContract
    name : null
    logger : null
    iconURL : null;

    constructor : (name) ->
      @name = name;
      @logger = log4javascript.getLogger(deviceTypeName + "Widget");
      @logger.removeAllAppenders();
      @logger.addAppender(new log4javascript.BrowserConsoleAppender());
      @iconURL = require.toUrl("./hob.jpg");

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
      return ((device.type() == "iCasa.Hob") || device.hasService("fr.liglab.adele.home.devices.kitchen.Hob"));

    getStatusWindowTemplateURL : () -> null;

    getDecorators : () -> null;

    propHasChanged : (device) ->
      null;

    init : (deviceViewModel) ->
      null;

  instance = hub.createInstance(HobWidget, {name : deviceTypeName + "Widget-1"});

  console.log(deviceTypeName + " module end loaded !!!");

  return instance;
);
