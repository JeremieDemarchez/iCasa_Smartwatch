(function() {
  var __hasProp = {}.hasOwnProperty,
    __extends = function(child, parent) { for (var key in parent) { if (__hasProp.call(parent, key)) child[key] = parent[key]; } function ctor() { this.constructor = child; } ctor.prototype = parent.prototype; child.prototype = new ctor(); child.__super__ = parent.prototype; return child; };

  define(["require", "hubu", "contracts/DeviceWidgetContract", "knockout", "log4javascript"], function(require, hub, DeviceWidgetContract, ko, log4javascript) {
    var FaucetWidget, instance;
    console.log("faucet module loaded !!!");
    FaucetWidget = (function(_super) {

      __extends(FaucetWidget, _super);

      FaucetWidget.prototype.name = null;

      FaucetWidget.prototype.logger = null;

      FaucetWidget.prototype.iconURL = null;

      function FaucetWidget(name) {
        this.name = name;
        this.logger = log4javascript.getLogger("FaucetWidget");
        this.logger.removeAllAppenders();
        this.logger.addAppender(new log4javascript.BrowserConsoleAppender());
        this.iconURL = require.toUrl("./faucet.png");
      }

      FaucetWidget.prototype.start = function() {
        return this.logger.info("FaucetWidget starting...");
      };

      FaucetWidget.prototype.stop = function() {
        return this.logger.info("FaucetWidget stoping...");
      };

      FaucetWidget.prototype.configure = function(theHub, config) {
        if (((config != null ? config.name : void 0) != null)) {
          this.name = config.name;
        }
        this.hub = theHub;
        return this.hub.provideService({
          component: this,
          contract: DeviceWidgetContract
        });
      };

      FaucetWidget.prototype.getComponentName = function() {
        return this.name;
      };

      FaucetWidget.prototype.getBaseIconURL = function() {
        return this.iconURL;
      };

      FaucetWidget.prototype.getCurrentIconURL = function() {
        return null;
      };

      FaucetWidget.prototype.manageDynamicIcon = function() {
        return false;
      };

      FaucetWidget.prototype.manageDevice = function(device) {
        return (device.type() === "iCasa.Faucet") || device.hasService("fr.liglab.adele.home.devices.water.Faucet");
      };

      FaucetWidget.prototype.getStatusWindowTemplateURL = function() {
        return null;
      };

      FaucetWidget.prototype.getDecorators = function() {
        return null;
      };

      FaucetWidget.prototype.propHasChanged = function(device) {
        return null;
      };

      FaucetWidget.prototype.init = function(deviceViewModel) {
        return null;
      };

      return FaucetWidget;

    })(DeviceWidgetContract);
    instance = hub.createInstance(FaucetWidget, {
      name: "faucetWidget-1"
    });
    console.log("faucet module end loaded !!!");
    return instance;
  });

}).call(this);
