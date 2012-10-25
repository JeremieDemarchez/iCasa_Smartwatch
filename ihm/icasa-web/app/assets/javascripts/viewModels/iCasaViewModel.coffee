
define(['knockout',
        'backbone',
        'knockback',
        'handlebars',
        'text!templates/deviceTable.html',
        'text!templates/personTable.html'],
  (ko, Backbone, kb, HandleBars, devTabHtml, personTabHtml) ->

    ko.bindingHandlers.handlebarTemplate = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element
            idx = 0;

            htmlString = Handlebars.compile(viewModel.tabTemplate);
            $(element).html(htmlString);
#            $(element).find(".changeInputToLabelWhenNoClick").addClass("tabCellIsNotEdited").before(
#                '<label class="changeInputToLabelWhenNoClick tabCellIsNotEdited" data-bind="text: name"></label>').click(() ->
#              $("this").toggleClass("tabCellIsEdited");
#            );

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called once when the binding is first applied to an element,
            # and again whenever the associated observable changes value.
    };

    # Data models

    Models = {};
    Collections = {};

    class Models.Device extends Backbone.Model
      urlRoot  : "/service/device"

    class Collections.Devices extends Backbone.Collection
      url : "/service/devices"
      model : Models.Device

    devicesCollection = new Collections.Devices();
    devicesCollection.fetch({
      success : (data) -> console.log(data);
      error : (err) -> throw err;
    });

#        feed = new EventSource('/service/devices/pushdata');
#        feed.addEventListener('message',
#          (event) ->
#            data = JSON.parse(event.data);
#            console.log("received data: " + event.data);
#
#            return
#
#          , false);

    # View models

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
