
define(['knockout', 'handlebars', 'text!templates/deviceTable.html'], (ko, HandleBars, devTableHtml) ->

    ko.bindingHandlers.handlebarTemplate = {
        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element


            htmlString = Handlebars.compile(devTableHtml);
            $(element).html(htmlString);

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called once when the binding is first applied to an element,
            # and again whenever the associated observable changes value.

            # TODO check removal is ok
    };

    class Zone
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);
            @isRoom = ko.observable(model.isRoom);

    class  Device
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);

    class Tab
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);
            @content = model.template;

    class ICasaViewModel
        constructor : (model) ->
            @zones = ko.observableArray([
                new Zone {
                    id: "livingroom",
                    name: "LivingRoom",
                    isRoom: true },
                new Zone {
                     id: "bathroom",
                     name: "Bathroom",
                     isRoom: true },
                new Zone {
                     id: "garden",
                     name: "Garden",
                     isRoom: false }
            ]);

            @devices = ko.observableArray([
                new Device {
                    id: "1",
                    name: "LivingRoom Temp Sensor" },
                new Device {
                    id: "2",
                    name: "Livebox" },
                new Device {
                    id: "3",
                    name: "iPod" }
            ]);

            @tabs = ko.observableArray([
                new Tab {
                    id: "devices",
                    name: "Devices" },
                new Tab {
                    id: "rooms",
                    name: "Rooms" },
                new Tab {
                    id: "zones",
                    name: "Zones" },
                new Tab {
                    id: "persons",
                    name: "Persons" },
                new Tab {
                    id: "script-player",
                    name: "Script Player" }
            ]);

            @removeDevice = (device) =>
              @devices.remove(device);

    return ICasaViewModel;
);
