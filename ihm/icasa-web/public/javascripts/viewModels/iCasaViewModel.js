/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 15/10/12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
define(['knockout', 'handlebars', 'text!templates/deviceTable.html'], function(ko, HandleBars, devTableHtml) {

    ko.bindingHandlers.handlebarTemplate = {
        init: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            // This will be called when the binding is first applied to an element


            var htmlString = Handlebars.compile(devTableHtml);
            $(element).html(htmlString);

            return { controlsDescendantBindings: false };
        },
        update: function(element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) {
            // This will be called once when the binding is first applied to an element,
            // and again whenever the associated observable changes value.
            //TODO check removal is ok
        }
    };

    function Zone(id, name, isRoom) {
        var self = this;

        self.id = id;
        self.name = ko.observable(name);
        self.isRoom = ko.observable(isRoom);
    }

    function Device(id, name) {
        var self = this;

        self.id = id;
        self.name = ko.observable(name);
    }

    function Tab(id, name) {
        var self = this;

        self.id = id;
        self.name = ko.observable(name);

        self.content = devTableHtml;
    }

    return function ICasaViewModel() {
        var self = this;

        self.zones = ko.observableArray([
            new Zone("livingroom", "LivingRoom", true),
            new Zone("bathroom", "Bathroom", true),
            new Zone("garden", "Garden", false)
        ]);

        self.devices = ko.observableArray([
            new Device("1", "LivingRoom Temp Sensor"),
            new Device("2", "Livebox"),
            new Device("3", "iPod")
        ]);

        self.tabs = ko.observableArray([
            new Tab("devices", "Devices"),
            new Tab("rooms", "Rooms"),
            new Tab("zones", "Zones"),
            new Tab("persons", "Persons"),
            new Tab("script-player", "Script Player")
        ]);

    };
});