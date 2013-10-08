(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var RefrigeratorWidget, deviceTypeName, instance;
    deviceTypeName = "Refrigerator";
    console.log(deviceTypeName + " module loaded !!!");
    RefrigeratorWidget = (function(_super) {

      __extends(RefrigeratorWidget, _super);

      RefrigeratorWidget.prototype.name = null;

      RefrigeratorWidget.prototype.logger = null;

      RefrigeratorWidget.prototype.iconURL = null;

      function RefrigeratorWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./Refrigerator.png");
      }

      RefrigeratorWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      RefrigeratorWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      RefrigeratorWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      RefrigeratorWidget.prototype.getComponentName = function() {
        return this.name;
      };

      RefrigeratorWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      RefrigeratorWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      RefrigeratorWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      RefrigeratorWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Refrigerator") || device.hasService("fr.liglab.adele.home.devices.kitchen.Refrigerator");
      };

      RefrigeratorWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      RefrigeratorWidget.prototype.getDecorators = function() {
        return null;
      };

      RefrigeratorWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      RefrigeratorWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return RefrigeratorWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(RefrigeratorWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
