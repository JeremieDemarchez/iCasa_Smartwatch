
define(['knockout',
        'knockback',
        'handlebars',
        'dataModels/ICasaDataModel'
        'text!templates/deviceTable.html',
        'text!templates/personTable.html',
        'text!templates/roomTable.html'],
  (ko, kb, HandleBars, DataModel, devTabHtml, personTabHtml, roomTabHtml) ->

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

    class ZoneViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'topY', 'bottomY', 'leftX', 'rightX']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.observable(model.name);
           @isRoom = kb.observable(model.isRoom);
           @leftX = kb.defaultObservable(@_leftX, 'Undefined');
           @rightX = kb.defaultObservable(@_rightX, 'Undefined');
           @bottomY = kb.defaultObservable(@_bottomY, 'Undefined');
           @topY = kb.defaultObservable(@_topY, 'Undefined');

    class DeviceTypeViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');

    class DeviceViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'positionX', 'positionY']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @positonX = kb.defaultObservable(@_positionX, 'Undefined');
           @positionY = kb.defaultObservable(@_positionY, 'Undefined');

    class PersonViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'positionX', 'positionY']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @positonX = kb.defaultObservable(@_positionX, 'Undefined');
           @positionY = kb.defaultObservable(@_positionY, 'Undefined');

    class TabViewModel extends kb.ViewModel
        constructor: (model) ->
           @id = model.id;
           @name = ko.observable(model.name);
           @tabTemplate = model.template;

    class ICasaViewModel extends kb.ViewModel
        constructor : (model) ->

           @devices = kb.collectionObservable(DataModel.collections.devices, {view_model: DeviceViewModel} );

           @persons = kb.collectionObservable(DataModel.collections.persons, {view_model: PersonViewModel});

           @zones = kb.collectionObservable(DataModel.collections.zones);

           @rooms = kb.collectionObservable(DataModel.collections.zones);

#             ko.computed( =>
#                return ko.utils.arrayFilter(@zones, (zone) ->
#                    return zone.isRoom());
#           );

           @tabs = ko.observableArray([
                new TabViewModel {
                    id: "devices",
                    name: "Devices",
                    template: devTabHtml},
                new TabViewModel {
                    id: "rooms",
                    name: "Rooms",
                    template: roomTabHtml},
                new TabViewModel {
                    id: "zones",
                    name: "Zones" ,
                    template: devTabHtml},
                new TabViewModel {
                    id: "persons",
                    name: "Persons" ,
                    template: personTabHtml},
                new TabViewModel {
                    id: "script-player",
                    name: "Script Player" ,
                    template: devTabHtml}
           ]);

           @newDeviceType = ko.observable("");

           @deviceTypes = kb.collectionObservable(DataModel.collections.deviceTypes, {view_model: DeviceTypeViewModel});
#           @deviceTypes.subscribe(@.selectFirstDeviceType)
#           selectFirstDeviceType: (models) =>
#              if models.length > 0
#                firstModel = models[0]
#                this.newDeviceType(firstModel)

#           @mapSize = ko.computed({
#              height: $("mapImg").height()
#              width: $("mapImg").width()
#           });

           @newDeviceName = ko.observable("");

           @createDevice = (iCasaViewModel) =>
              newDevice = new DataModel.Models.Device({ id: iCasaViewModel.newDeviceName(), name: iCasaViewModel.newDeviceName(), "type": iCasaViewModel.newDeviceType() });
              newDevice.save();
              newDevice.fetch();
              @devices.push(new DeviceViewModel(newDevice));

           @removeDevice = (device) =>
              @devices.remove(device);
              @devices.fetch();

           @createPerson = (iCasaViewModel) =>
              newPerson = new DataModel.Models.Person({ id: iCasaViewModel.newPersonName(), name: iCasaViewModel.newPersonName() });
              newPerson.save();
              newPerson.fetch();
              @devices.push(new PersonViewModel(newPerson));

           @removePerson = (person) =>
              @persons.remove(person);
              @persons.fetch();

    return ICasaViewModel;
);
