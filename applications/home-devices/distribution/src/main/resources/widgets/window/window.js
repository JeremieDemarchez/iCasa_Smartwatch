(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var DoorSensorWidget, instance;
    console.log("door module loaded !!!");
    DoorSensorWidget = (function(_super) {

      __extends(DoorSensorWidget, _super);

      DoorSensorWidget.prototype.name = null;

      DoorSensorWidget.prototype.logger = null;

      DoorSensorWidget.prototype.iconURL = null;

      function DoorSensorWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger("DoorSensorWidget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./door_icon.jpg");
      }

      DoorSensorWidget.prototype.start = function() {
        return this.logger.info("DoorSensorWidget starting...");
      };

      DoorSensorWidget.prototype.stop = function() {
        return this.logger.info("DoorSensorWidget stoping...");
      };

      DoorSensorWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      DoorSensorWidget.prototype.getComponentName = function() {
        return this.name;
      };

      DoorSensorWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      DoorSensorWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      DoorSensorWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      DoorSensorWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Door") || device.hasService("fr.liglab.adele.home.devices.general.Door");
      };

      DoorSensorWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      DoorSensorWidget.prototype.getDecorators = function() {
        return null;
      };

      DoorSensorWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      DoorSensorWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return DoorSensorWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(DoorSensorWidget, {
      name: "doorSensorWidget-1"
    });
    console.log("doorSensor module end loaded !!!");
    return instance;
  });

}).call(this);
