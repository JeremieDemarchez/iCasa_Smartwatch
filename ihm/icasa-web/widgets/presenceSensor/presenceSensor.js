(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "log4javascript"], function(require, hub, DeviceWidgetContract, log4javascript) {
    var PresenceSensorWidget, instance;
    console.log("presenceSensor module loaded !!!");
    PresenceSensorWidget = (function(_super) {

      __extends(PresenceSensorWidget, _super);

      PresenceSensorWidget.prototype.name = null;

      PresenceSensorWidget.prototype.logger = null;

      PresenceSensorWidget.prototype.iconURL = null;

      function PresenceSensorWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger("PresenceSensorWidget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./movementDetector.png");
      }

      PresenceSensorWidget.prototype.start = function() {
        return this.logger.info("PresenceSensorWidget starting...");
      };

      PresenceSensorWidget.prototype.stop = function() {
        return this.logger.info("PresenceSensorWidget stoping...");
      };

      PresenceSensorWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      PresenceSensorWidget.prototype.getComponentName = function() {
        return this.name;
      };

      PresenceSensorWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      PresenceSensorWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      PresenceSensorWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      PresenceSensorWidget.prototype.manageDevice = function(device) {
        return (this.type() === "iCasa.PresenceSensor") || this.hasService("fr.liglab.adele.icasa.device.presence.PresenceSensor");
      };

      PresenceSensorWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      PresenceSensorWidget.prototype.getDecorators = function() {
        return null;
      };

      PresenceSensorWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return PresenceSensorWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(PresenceSensorWidget, {
      name: "presenceSensorWidget-1"
    });
    console.log("presenceSensor module end loaded !!!");
    return instance;
  });

}).call(this);
