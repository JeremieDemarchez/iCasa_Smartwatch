
define(['knockout',
        'knockback',
        'handlebars',
        'text!templates/deviceTable.html',
        'text!templates/personTable.html'],
  (ko, kb, HandleBars, devTabHtml, personTabHtml) ->

    ko.bindingHandlers.handlebarTemplate = {
        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element


            htmlString = Handlebars.compile(viewModel.tabTemplate);
            $(element).html(htmlString);

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called once when the binding is first applied to an element,
            # and again whenever the associated observable changes value.
    };

    class Zone extends kb.ViewModel
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);
            @isRoom = ko.observable(model.isRoom);

    class Device extends kb.ViewModel
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);

    class Person extends kb.ViewModel
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);

    class Tab extends kb.ViewModel
        constructor: (model) ->
            @id = model.id;
            @name = ko.observable(model.name);
            @tabTemplate = model.template;

    class ICasaViewModel extends kb.ViewModel
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

            @persons = ko.observableArray([
                new Person {
                    id: "paul",
                    name: "Paul" }
            ]);

            @tabs = ko.observableArray([
                new Tab {
                    id: "devices",
                    name: "Devices",
                    template: devTabHtml},
                new Tab {
                    id: "rooms",
                    name: "Rooms",
                    template: devTabHtml},
                new Tab {
                    id: "zones",
                    name: "Zones" ,
                    template: devTabHtml},
                new Tab {
                    id: "persons",
                    name: "Persons" ,
                    template: personTabHtml},
                new Tab {
                    id: "script-player",
                    name: "Script Player" ,
                    template: devTabHtml}
            ]);

            @removeDevice = (device) =>
              @devices.remove(device);

            @removePerson = (person) =>
              @persons.remove(person);

    return ICasaViewModel;
);
