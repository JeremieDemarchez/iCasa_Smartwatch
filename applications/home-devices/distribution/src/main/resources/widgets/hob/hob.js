(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var HobWidget, deviceTypeName, instance;
    deviceTypeName = "Hob";
    console.log(deviceTypeName + " module loaded !!!");
    HobWidget = (function(_super) {

      __extends(HobWidget, _super);

      HobWidget.prototype.name = null;

      HobWidget.prototype.logger = null;

      HobWidget.prototype.iconURL = null;

      function HobWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./hob.jpg");
      }

      HobWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      HobWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      HobWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      HobWidget.prototype.getComponentName = function() {
        return this.name;
      };

      HobWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      HobWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      HobWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      HobWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Hob") || device.hasService("fr.liglab.adele.home.devices.kitchen.Hob");
      };

      HobWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      HobWidget.prototype.getDecorators = function() {
        return null;
      };

      HobWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      HobWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return HobWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(HobWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
