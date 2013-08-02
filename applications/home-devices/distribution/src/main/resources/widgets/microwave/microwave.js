(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var MicrowaveWidget, deviceTypeName, instance;
    deviceTypeName = "Microwave";
    console.log(deviceTypeName + " module loaded !!!");
    MicrowaveWidget = (function(_super) {

      __extends(MicrowaveWidget, _super);

      MicrowaveWidget.prototype.name = null;

      MicrowaveWidget.prototype.logger = null;

      MicrowaveWidget.prototype.iconURL = null;

      function MicrowaveWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./Microwave.png");
      }

      MicrowaveWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      MicrowaveWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      MicrowaveWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      MicrowaveWidget.prototype.getComponentName = function() {
        return this.name;
      };

      MicrowaveWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      MicrowaveWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      MicrowaveWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      MicrowaveWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Microwave") || device.hasService("fr.liglab.adele.home.devices.kitchen.Microwave");
      };

      MicrowaveWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      MicrowaveWidget.prototype.getDecorators = function() {
        return null;
      };

      MicrowaveWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      MicrowaveWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return MicrowaveWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(MicrowaveWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
