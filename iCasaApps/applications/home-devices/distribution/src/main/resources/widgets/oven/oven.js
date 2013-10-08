(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var OvenWidget, deviceTypeName, instance;
    deviceTypeName = "Oven";
    console.log(deviceTypeName + " module loaded !!!");
    OvenWidget = (function(_super) {

      __extends(OvenWidget, _super);

      OvenWidget.prototype.name = null;

      OvenWidget.prototype.logger = null;

      OvenWidget.prototype.iconURL = null;

      function OvenWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./oven-icon.png");
      }

      OvenWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      OvenWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      OvenWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      OvenWidget.prototype.getComponentName = function() {
        return this.name;
      };

      OvenWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      OvenWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      OvenWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      OvenWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Oven") || device.hasService("fr.liglab.adele.home.devices.kitchen.Oven");
      };

      OvenWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      OvenWidget.prototype.getDecorators = function() {
        return null;
      };

      OvenWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      OvenWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return OvenWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(OvenWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
