
define(['jquery',
        'jquery.ui',
        'backbone',
        'knockout',
        'knockback',
        'handlebars',
        'dataModels/ICasaDataModel'
        'text!templates/deviceTable.html',
        'text!templates/personTable.html',
        'text!templates/roomTable.html',
        'text!templates/zoneTable.html',
        'text!templates/scriptPlayer.html',
        'text!templates/tabs.html'],
  ($, ui, Backbone, ko, kb, HandleBars, DataModel, devTabHtml, personTabHtml, roomTabHtml, zoneTabHtml, scriptPlayerHtml, tabsTemplateHtml) ->

    # HTML custom bindings

    ko.bindingHandlers.handlebarTemplate = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element
            idx = 0;

            template = HandleBars.compile(viewModel.tabTemplate, {});
            $(element).html(template);
#            $(element).find(".changeInputToLabelWhenNoClick").addClass("tabCellIsNotEdited").before(
#                '<label class="changeInputToLabelWhenNoClick tabCellIsNotEdited" data-bind="text: name"></label>').click(() ->
#              $("this").toggleClass("tabCellIsEdited");
#            );

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called once when the binding is first applied to an element,
            # and again whenever the associated observable changes value.
    };

    ko.bindingHandlers.jqueryTabs = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            tabHeadersTemplate = HandleBars.compile(tabsTemplateHtml);
            htmlString = tabHeadersTemplate();
            addTabDiv = (tab) ->
                tabContent = tab.tabTemplate;
                tabTemplateParams = { tabId: tab.tabHtmlId(), content: tabContent };
                htmlString = htmlString + '<div id="' + tab.tabHtmlId() + '">' + tabContent + '</div>';

            addTabDiv(tab) for tab in viewModel.tabs();

            $(element).html(htmlString);


            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called once when the binding is first applied to an element,
            # and again whenever the associated observable changes value.
    };

    # View models

    class ZoneViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'topY', 'bottomY', 'leftX', 'rightX', 'isRoom']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @isRoom = kb.defaultObservable(@_isRoom, 'Undefined');
           @leftX = kb.defaultObservable(@_leftX, 'Undefined');
           @rightX = kb.defaultObservable(@_rightX, 'Undefined');
           @bottomY = kb.defaultObservable(@_bottomY, 'Undefined');
           @topY = kb.defaultObservable(@_topY, 'Undefined');

    class ScriptViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'state']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @state = kb.defaultObservable(@_state, 'undefined');

    class DecoratorViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'show', 'imgSrc', 'positionX', 'positionY', 'width', 'height', 'styleLeft', 'styleTop']})
           @id = kb.defaultObservable(@_id, model.name);
           @name = kb.defaultObservable(@_name, 'state');
           @show = kb.defaultObservable(@_show, false);
           @imgSrc = kb.defaultObservable(@_imgSrc, '/assets/images/devices/decorators/play.png');
           @positionX = kb.defaultObservable(@_positionX, 16);
           @positionY = kb.defaultObservable(@_positionY, 16);
           @styleLeft = ko.computed({
              read: () =>
                  return @positionX() + "px";
              write: (value) =>
                  value = parseInt(value.replace(/px/, ""));
                  if (!isNaN(value))
                    @positionX(value);
              owner: @
           }
           , @)
           @styleTop = ko.computed({
              read: () =>
                  return @positionY() + "px";
              write: (value) =>
                  value = parseInt(value.replace(/px/, ""));
                  if (!isNaN(value))
                    @positionY(value);
              owner: @
           }
           , @)
           @width = kb.defaultObservable(@_width, '15px');
           @height = kb.defaultObservable(@_height, '15px');

    class DeviceTypeViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');

    class DeviceViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'positionX', 'positionY', 'type', 'location', 'state', 'fault']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @location = kb.defaultObservable(@_location, 'Undefined');
           @state = kb.defaultObservable(@_state, 'activated');
           @fault = kb.defaultObservable(@_fault, 'no');
           @positionX = kb.defaultObservable(@_positionX, 0);
           @positionY = kb.defaultObservable(@_positionY, 0);
           @styleLeft = ko.computed({
              read: () =>
                  return @positionX() + "px";
              write: (value) =>
                  value = parseInt(value.replace(/px/, ""));
                  if (!isNaN(value))
                    @positionX(value);
              owner: @
           }
           , @)
           @styleTop = ko.computed({
              read: () =>
                return @positionY() + "px";
              write: (value) =>
                value = parseInt(value.replace(/px/, ""));
                if (!isNaN(value))
                  @positionY(value);
              owner: @
           }
           , @)
           @type = kb.defaultObservable(@_type, 'Undefined');
           @imgSrc = ko.computed(() =>
              imgName = "NewDevice";
              if (@type() == "iCASA.Cooler")
                 imgName = "Cooler";
              if (@type() == "iCASA.AudioSource")
                 imgName = "Player";
              if (@type() == "iCASA.DimmerLight")
                 imgName = "DimmerLamp";
              if (@type() == "iCASA.Thermometer")
                 imgName = "Thermometer";
              if (@type() == "iCASA.Heater")
                 imgName = "Heater";
              if (@type() == "iCASA.Photometer")
                 imgName = "Photometer";
              if (@type() == "iCASA.BinaryLight")
                 imgName = "Lamp";
              if (@type() == "iCASA.PresenceSensor")
                 imgName = "Presence";
              if (@type() == "iCASA.Speaker")
                 imgName = "Speaker";
              if (@type() == "iCASA.Power")
                 imgName = "Power";

              return "/assets/images/devices/" + imgName + ".png";
           , @);
           @decorators = ko.observableArray([
                new DecoratorViewModel new Backbone.Model {
                    name: "event",
                    imgSrc: '/assets/images/devices/decorators/event.png',
                    show: false}
                new DecoratorViewModel new Backbone.Model {
                    name: "deactivated",
                    imgSrc: '/assets/images/devices/decorators/stop.png',
                    show: false},
                new DecoratorViewModel new Backbone.Model {
                    name: "fault",
                    imgSrc: '/assets/images/devices/decorators/warning.png',
                    show: false},
                new DecoratorViewModel new Backbone.Model {
                    name: "activated",
                    imgSrc: '/assets/images/devices/decorators/play.png',
                    show: true}
           ]);
           @updateWidget= (newValue) =>
                activatedState = false;
                if ("activated" == @state())
                    activatedState = true;
                faultState = false;
                if ("yes" == @fault())
                    faultState = true;

                ko.utils.arrayForEach(@decorators(), (decorator) ->
                    if (decorator.name() == "activated")
                        decorator.show(activatedState && !faultState);
                    if (decorator.name() == "fault")
                        decorator.show(activatedState && faultState);
                    if (decorator.name() == "deactivated")
                        decorator.show(!activatedState);
                );
           # init
           @state.subscribe(@updateWidget);
           @fault.subscribe(@updateWidget);
           @updateWidget();

    class PersonViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'positionX', 'positionY', 'location']})
           @id = kb.defaultObservable(@_id, 'Undefined');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @location = kb.defaultObservable(@_location, 'Undefined');
           @positionX = kb.defaultObservable(@_positionX, 'Undefined');
           @positionY = kb.defaultObservable(@_positionY, 'Undefined');
           @styleLeft = ko.computed({
              read: () =>
                return @positionX() + "px";
              write: (value) =>
                value = parseInt(value.replace(/px/, ""));
                if (!isNaN(value))
                  @positionX(value);
              owner: @
           }
           , @)
           @styleTop = ko.computed({
              read: () =>
                return @positionY() + "px";
              write: (value) =>
                value = parseInt(value.replace(/px/, ""));
                if (!isNaN(value))
                  @positionY(value);
              owner: @
           }
           , @)
           @imgSrc = "/assets/images/users/user2.png";
           @decorators = ko.observableArray([  ]);

    class TabViewModel extends kb.ViewModel
        constructor: (model) ->
           @id = model.id;
           @name = ko.observable(model.name);
           @tabTemplate = model.template;
           @tabHtmlId = ko.computed( () =>
               return "tab-" + @id;
           , @);
           @tabHtmlIdRef = ko.computed( () =>
               return "#" + @tabHtmlId();
           , @);

    class ICasaViewModel extends kb.ViewModel
        constructor : (model) ->

           @devices = kb.collectionObservable(DataModel.collections.devices, {view_model: DeviceViewModel} );

           @persons = kb.collectionObservable(DataModel.collections.persons, {view_model: PersonViewModel});

           @zones = kb.collectionObservable(DataModel.collections.zones, {view_model: ZoneViewModel});

           @notRoomZones = ko.computed(() =>
                return ko.utils.arrayFilter(@zones, (zone) ->
                    return !zone.isRoom();
                );
           , @);

           @rooms = ko.computed(() =>
                return ko.utils.arrayFilter(@zones, (zone) ->
                    return zone.isRoom();
                );
           , @);

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
                    template: zoneTabHtml},
                new TabViewModel {
                    id: "persons",
                    name: "Persons" ,
                    template: personTabHtml},
                new TabViewModel {
                    id: "script-player",
                    name: "Script Player" ,
                    template: scriptPlayerHtml}
           ]);

           @scripts = kb.collectionObservable(DataModel.collections.scripts, {view_model: ScriptViewModel});

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

           @deviceActivationStates = ko.observableArray([
              'activated',
              'deactivated',
              'unknown'
           ]);

           @deviceFaults = ko.observableArray([
              'yes',
              'no',
              'unknown'
           ]);

           @newDeviceName = ko.observable("");

           @createDevice = () =>
              newDevice = new DataModel.Models.Device({ id: @newDeviceName(), name: @newDeviceName(), "type": @newDeviceType() });
              newDevice.save();
              DataModel.Collections.fetch();

           @removeDevice = (device) =>
              device.destroy() unless device.isNew()
              return false;

           @showDeviceWindow = (device) =>
              DataModel.collections.devices.fetch();

           @newPersonName = ko.observable("");

           @createPerson = () =>
              newPerson = new DataModel.Models.Person({ id: @newPersonName(), name: @newPersonName() });
              newPerson.save();
              newPerson.fetch();
              @devices.push(new PersonViewModel(newPerson));

           @removePerson = (person) =>
              @persons.remove(person);
              @persons.fetch();

           @showPersonWindow = (person) =>
              DataModel.collections.persons.fetch();

           @newZoneName = ko.observable("");

           @createZone = () =>
              newZone = new DataModel.Models.Zone({ id: @newZoneName(), name: @newZoneName(), isRoom: false });
              newZone.save();
              newZone.fetch();
              @zones.push(new ZoneViewModel(newZone));

           @newRoomName = ko.observable("");

           @createRoom = () =>
              newZone = new DataModel.Models.Zone({ id: @newRoomName(), name: @newRoomName(), isRoom: true });
              newZone.save();
              newZone.fetch();
              @zones.push(new ZoneViewModel(newZone));

           @removeZone = (zone) =>
              @zones.remove(zone);
              @zones.fetch();

           @selectedScript = ko.observable();

           @selectedScriptState = ko.computed( () =>
              if (@selectedScript())
                  return @selectedScript().state();
              else
                  return 'undefined';
           );

           @scriptStates = ko.observableArray([
              'started',
              'stopped',
              'paused',
              'undefined'
           ]);

           @startScript = () =>
              @selectedScript().state('started');
              @selectedScript().model().save();

           @stopScript = () =>
              @selectedScript().state('stopped');
              @selectedScript().model().save();

           @pauseScript = () =>
              @selectedScript().state('paused');
              @selectedScript().model().save();

    return ICasaViewModel;
);
