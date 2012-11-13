
define(['knockout',
        'knockback',
        'handlebars',
        'dataModels/ICasaDataModel'
        'text!templates/deviceTable.html',
        'text!templates/personTable.html'],
  (ko, kb, HandleBars, DataModel, devTabHtml, personTabHtml) ->

    # HTML custom bindings

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

    # View models

    class Zone extends kb.ViewModel
        constructor: (model) ->
          @id = model.id;
          @name = ko.observable(model.name);
          @isRoom = ko.observable(model.isRoom);

    class DeviceType extends kb.ViewModel
      constructor: (model) ->
        super(model, {internals: ['id', 'name']})
        @id = kb.defaultObservable(@_id, 'Undefined');
        @name = kb.defaultObservable(@_name, 'Undefined');

    class Device extends kb.ViewModel
        constructor: (model) ->
          super(model, {internals: ['id', 'name', 'positionX', 'positionY']})
          @id = kb.defaultObservable(@_id, 'Undefined');
          @name = kb.defaultObservable(@_name, 'Undefined');
          @positonX = kb.defaultObservable(@_positionX, 'Undefined');
          @positionY = kb.defaultObservable(@_positionY, 'Undefined');

    class Person extends kb.ViewModel
      constructor: (model) ->
        super(model, {internals: ['id', 'name', 'positionX', 'positionY']})
        @id = kb.defaultObservable(@_id, 'Undefined');
        @name = kb.defaultObservable(@_name, 'Undefined');
        @positonX = kb.defaultObservable(@_positionX, 'Undefined');
        @positionY = kb.defaultObservable(@_positionY, 'Undefined');

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

            @devices = kb.collectionObservable(DataModel.collections.devices);

            @persons = kb.collectionObservable(DataModel.collections.persons);

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

            @deviceTypes = kb.collectionObservable(DataModel.collections.deviceTypes);

#            @mapSize = ko.computed({
#              height: $("mapImg").height()
#              width: $("mapImg").width()
#            });

            @newDeviceType = ko.observable("iCasa.DimmerLight");

            @newDeviceName = ko.observable("");

            @createDevice = (iCasaViewModel) =>
              newDevice = new DataModel.Models.Device({ id: iCasaViewModel.newDeviceName(), name: iCasaViewModel.newDeviceName(), "type": iCasaViewModel.newDeviceType() });
              newDevice.save();
              newDevice.fetch();
              @devices.push(new Device(newDevice));

            @removeDevice = (device) =>
              @devices.remove(device);
              @devices.fetch();

            @createPerson = (iCasaViewModel) =>
              newPerson = new DataModel.Models.Person({ id: iCasaViewModel.newPersonName(), name: iCasaViewModel.newPersonName() });
              newPerson.save();
              newPerson.fetch();
              @devices.push(new Person(newPerson));

            @removePerson = (person) =>
              @persons.remove(person);
              @persons.fetch();

    return ICasaViewModel;
);
