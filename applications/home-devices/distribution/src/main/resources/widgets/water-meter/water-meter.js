(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var WaterMeterWidget, deviceTypeName, instance;
    deviceTypeName = "WaterMeter";
    console.log(deviceTypeName + " module loaded !!!");
    WaterMeterWidget = (function(_super) {

      __extends(WaterMeterWidget, _super);

      WaterMeterWidget.prototype.name = null;

      WaterMeterWidget.prototype.logger = null;

      WaterMeterWidget.prototype.iconURL = null;

      function WaterMeterWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./water-meter.png");
      }

      WaterMeterWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      WaterMeterWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      WaterMeterWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      WaterMeterWidget.prototype.getComponentName = function() {
        return this.name;
      };

      WaterMeterWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      WaterMeterWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      WaterMeterWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      WaterMeterWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.WaterMeter") || device.hasService("fr.liglab.adele.home.devices.water.WaterMeter");
      };

      WaterMeterWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      WaterMeterWidget.prototype.getDecorators = function() {
        return null;
      };

      WaterMeterWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      WaterMeterWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return WaterMeterWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(WaterMeterWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
