(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var FlushToiletWidget, deviceTypeName, instance;
    deviceTypeName = "FlushToilet";
    console.log("door module loaded !!!");
    FlushToiletWidget = (function(_super) {

      __extends(FlushToiletWidget, _super);

      FlushToiletWidget.prototype.name = null;

      FlushToiletWidget.prototype.logger = null;

      FlushToiletWidget.prototype.iconURL = null;

      function FlushToiletWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./flush-toilet.jpg");
      }

      FlushToiletWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      FlushToiletWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      FlushToiletWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      FlushToiletWidget.prototype.getComponentName = function() {
        return this.name;
      };

      FlushToiletWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      FlushToiletWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      FlushToiletWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      FlushToiletWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.FlushToilet") || device.hasService("fr.liglab.adele.home.devices.water.FlushToilet");
      };

      FlushToiletWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      FlushToiletWidget.prototype.getDecorators = function() {
        return null;
      };

      FlushToiletWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      FlushToiletWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return FlushToiletWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(FlushToiletWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
