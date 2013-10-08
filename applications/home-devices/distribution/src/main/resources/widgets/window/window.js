(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var WindowWidget, deviceTypeName, instance;
    deviceTypeName = "Window";
    console.log(deviceTypeName + " module loaded !!!");
    WindowWidget = (function(_super) {

      __extends(WindowWidget, _super);

      WindowWidget.prototype.name = null;

      WindowWidget.prototype.logger = null;

      WindowWidget.prototype.iconURL = null;

      function WindowWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger(deviceTypeName + "Widget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./window.jpg");
      }

      WindowWidget.prototype.start = function() {
        return this.logger.info(deviceTypeName + "Widget starting...");
      };

      WindowWidget.prototype.stop = function() {
        return this.logger.info(deviceTypeName + "Widget stoping...");
      };

      WindowWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      WindowWidget.prototype.getComponentName = function() {
        return this.name;
      };

      WindowWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      WindowWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      WindowWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      WindowWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Window") || device.hasService("fr.liglab.adele.home.devices.general.Window");
      };

      WindowWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      WindowWidget.prototype.getDecorators = function() {
        return null;
      };

      WindowWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      WindowWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return WindowWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(WindowWidget, {
      name: deviceTypeName + "Widget-1"
    });
    console.log(deviceTypeName + " module end loaded !!!");
    return instance;
  });

}).call(this);
