
define(['jquery',
        'jquery.ui',
        'backbone',
        'knockout',
        'knockback',
        'handlebars',
        'dataModels/ICasaDataModel'
        'text!templates/deviceTable.html',
        'text!templates/personTable.html',
        'text!templates/zoneTable.html',
        'text!templates/scriptPlayer.html',
        'text!templates/tabs.html',
        'text!templates/deviceStatusWindow.html',
        'text!templates/personStatusWindow.html',
        'domReady'],
  ($, ui, Backbone, ko, kb, HandleBars, DataModel, devTabHtml, personTabHtml, zoneTabHtml, scriptPlayerHtml, tabsTemplateHtml, deviceStatusWindowTemplateHtml, personStatusWindowTemplateHtml) ->

    # HTML custom bindings

    ko.bindingHandlers.staticTemplate = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->

            # Next, whether or not the supplied model property is observable, get its current value
            valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
            if valueUnwrapped.template == undefined
              return {controlsDescendantBindings: true};

            $(element).html(valueUnwrapped.template);

            innerBindingContext = bindingContext.extend(valueUnwrapped.data);
            ko.applyBindingsToDescendants(innerBindingContext, element);

            return { controlsDescendantBindings: true };

    };


    ko.bindingHandlers.handlebarTemplate = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            value = valueAccessor();

            # Next, whether or not the supplied model property is observable, get its current value
            valueUnwrapped = ko.utils.unwrapObservable(value);

            template = HandleBars.compile(valueUnwrapped, {});
            $(element).html(template);

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            value = valueAccessor();

            # Next, whether or not the supplied model property is observable, get its current value
            valueUnwrapped = ko.utils.unwrapObservable(value);

            template = HandleBars.compile(valueUnwrapped, {});
            $(element).html(template);
    };

    ko.bindingHandlers.jqueryDraggable = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            $(element).draggable( {
                compartment: "#mapContainer",
                scroll: true,
                opacity: 0.70,
                stop: (event, eventUI) ->
                  viewModel.positionX(eventUI.position.left);
                  viewModel.positionY(eventUI.position.top);
                  viewModel.model().save();
            });

            return { controlsDescendantBindings: false };
    };

    ko.bindingHandlers.jqueryDialog = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            titleUnwrapped = ko.utils.unwrapObservable(valueAccessor());

            $(element).dialog({
                autoOpen: false,
                title: titleUnwrapped
            });
#            $(element).dialog({
#                autoOpen: false,
#                position: {my: "center", at: "center", of: "#statusWindows"},
#                title: titleUnwrapped
#            })
#            .parent().resizable({
#                containment: "#statusWindows"
#            }).draggable({
#                containment: "#statusWindows",
#                opacity: 0.70
#            });

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            titleUnwrapped = ko.utils.unwrapObservable(valueAccessor());
            $(element).dialog("option", "title", titleUnwrapped);

            visibleUnwrapped = ko.utils.unwrapObservable(viewModel.statusWindowVisible);

            if (visibleUnwrapped)
              $(element).dialog( "open" );
            else
              $(element).dialog( "close" );

            return { controlsDescendantBindings: false };
    };

    ko.bindingHandlers.jqueryTooltip = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            titleUnwrapped = ko.utils.unwrapObservable(valueAccessor());

            $(element).tooltip(
                content: titleUnwrapped,
                items: "img[alt]"
            );

            return { controlsDescendantBindings: false };

        update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            titleUnwrapped = ko.utils.unwrapObservable(valueAccessor());

            $(element).tooltip( "option", "content", titleUnwrapped);

            return { controlsDescendantBindings: false };
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
    };

    # View models

    class ZoneViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'topY', 'bottomY', 'leftX', 'rightX', 'isRoom']})
           @id = kb.observable(model, 'id');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @isRoom = kb.defaultObservable(@_isRoom, 'Undefined');
           @leftX = kb.defaultObservable(@_leftX, 'Undefined');
           @rightX = kb.defaultObservable(@_rightX, 'Undefined');
           @bottomY = kb.defaultObservable(@_bottomY, 'Undefined');
           @topY = kb.defaultObservable(@_topY, 'Undefined');

    class ScriptViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'state']})
           @id = kb.observable(model, 'id');
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
              owner: @
           }
           , @)
           @styleTop = ko.computed({
              read: () =>
                  return @positionY() + "px";
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
           super(model, {internals: ['id', 'name', 'positionX', 'positionY', 'type', 'location', 'state', 'fault', 'statusWindowVisible', 'statusWindowTemplate']})
           @id = kb.observable(model, 'id');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @location = kb.defaultObservable(@_location, 'Undefined');
           @state = kb.defaultObservable(@_state, 'activated');
           @isDesactivated = ko.computed({
              read: () =>
                return @state() == "deactivated";
              owner: @
           }
           , @);
           @fault = kb.defaultObservable(@_fault, 'no');
           @positionX = kb.defaultObservable(@_positionX, 0);
           @positionY = kb.defaultObservable(@_positionY, 0);
           @styleLeft = ko.computed({
              read: () =>
                return @positionX() + "px";
              owner: @
           }
           , @);
           @styleTop = ko.computed({
              read: () =>
                return @positionY() + "px";
              owner: @
           }
           , @);
           @zones = kb.collectionObservable(DataModel.collections.zones, {view_model: ZoneViewModel});
           @locationZone = ko.computed({
              read: () =>
                zoneModel = @zones.collection().get(@location());
                if (zoneModel == undefined)
                  return null;
                return @zones.viewModelByModel(zoneModel);
              write: (zone) =>
                if (zone != undefined)
                  zoneName = zone.name();
                  @location(zoneName);
                  @.model().save();
                return zone;
              owner: @
           }
           );
           @tooltipContent = ko.computed( () =>
              return @name() + " /n" + @id();
           , @);
           @statusWindowTitle = ko.computed( () =>
             return @name();
           , @);
           @statusWindowTemplate = deviceStatusWindowTemplateHtml;
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
           @statusWindowVisible = kb.defaultObservable(@_statusWindowVisible, false);
           @updateWidgetImg= (newValue) =>
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
#                    if (decorator.name() == "deactivated")
#                        decorator.show(!activatedState);
                );
           @isHighlighted = ko.observable(false);
           @saveModel= (newValue) =>
                @.model().save();
           # init
           @state.subscribe(@updateWidgetImg);
           @fault.subscribe(@updateWidgetImg);
           @updateWidgetImg();


    class PersonViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model, {internals: ['id', 'name', 'positionX', 'positionY', 'location', 'statusWindowVisible', 'statusWindowTemplate']})
           @id = kb.observable(model, 'id');
           @name = kb.defaultObservable(@_name, 'Undefined');
           @location = kb.defaultObservable(@_location, 'Undefined');
           @positionX = kb.defaultObservable(@_positionX, 'Undefined');
           @positionY = kb.defaultObservable(@_positionY, 'Undefined');
           @styleLeft = ko.computed({
              read: () =>
                return @positionX() + "px";
              owner: @
           }
           , @)
           @styleTop = ko.computed({
              read: () =>
                return @positionY() + "px";
              owner: @
           }
           , @)
           @zones = kb.collectionObservable(DataModel.collections.zones, {view_model: ZoneViewModel});
           @locationZone = ko.computed({
              read: () =>
                zoneModel = @zones.collection().get(@location());
                if (zoneModel == undefined)
                  return null;
                return @zones.viewModelByModel(zoneModel);
              write: (zone) =>
                if (zone != undefined)
                  zoneName = zone.name();
                  @location(zoneName);
                  @.model().save();
                return zone;
              owner: @
           }
           );
           @tooltipContent = ko.computed( () =>
              return @name() + " /n" + @id();
           , @);
           @statusWindowTitle = ko.computed( () =>
              return @name();
           , @);
           @statusWindowTemplate = personStatusWindowTemplateHtml;
           @statusWindowVisible = kb.defaultObservable(@_statusWindowVisible, false);
           @imgSrc = "/assets/images/users/user2.png";
           @decorators = ko.observableArray([  ]);
           @isHighlighted = ko.observable(false);
           #@highlights = @isHighlighted(true);

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
              newDevice = new DataModel.Models.Device({ deviceId: @newDeviceName(), name: @newDeviceName(), "type": @newDeviceType() });
              DataModel.collections.devices.push(newDevice);
              newDevice.save();

           @removeDevice = (device) =>
              device.model().destroy();

           @showDeviceWindow = (device) =>
              device.statusWindowVisible(false);
              device.statusWindowVisible(true);

           @newPersonName = ko.observable("");

           @createPerson = () =>
              newPerson = new DataModel.Models.Person({ personId: @newPersonName(), name: @newPersonName(), positionX: 1, positionY: 1 });
              DataModel.collections.persons.push(newPerson);
              newPerson.save();

           @removePerson = (person) =>
              person.model().destroy();

           @showPersonWindow = (person) =>
              person.statusWindowVisible(false);
              person.statusWindowVisible(true);

           @newZoneName = ko.observable("");

           @createZone = () =>
              newZone = new DataModel.Models.Zone({ deviceId: @newZoneName(), name: @newZoneName(), isRoom: false, leftX: 1, topY: 1, rightX : 21, bottomY: 21 });
              DataModel.collections.zones.push(newZone);
              newZone.save();

           @newRoomName = ko.observable("");

           @createRoom = () =>
              newZone = new DataModel.Models.Zone({ zoneId: @newRoomName(), name: @newRoomName(), isRoom: true, leftX: 1, topY: 1, rightX : 21, bottomY: 21 });
              @zones.push(new ZoneViewModel(newZone));
              newZone.save();

           @removeZone = (zone) =>
              zone.model().destroy();

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
