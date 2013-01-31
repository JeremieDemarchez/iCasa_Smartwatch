
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
        'text!templates/zoneStatusWindow.html',
        'domReady'],
  ($, ui, Backbone, ko, kb, HandleBars, DataModel, devTabHtml, personTabHtml, zoneTabHtml, scriptPlayerHtml, tabsTemplateHtml, deviceStatusWindowTemplateHtml, personStatusWindowTemplateHtml, zoneStatusWindowTemplateHtml, bathroomScaleStatusWindowTemplateHtml) ->

    # HTML custom bindings

    ko.bindingHandlers.staticTemplate = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->

            # Next, whether or not the supplied model property is observable, get its current value
            valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
            if valueUnwrapped.template == undefined
              return {controlsDescendantBindings: true};

            templateUnwrapped = ko.utils.unwrapObservable(valueUnwrapped.template);
            $(element).html(templateUnwrapped);

            innerBindingContext = bindingContext.extend(valueUnwrapped.data);
            ko.applyBindingsToDescendants(innerBindingContext, element);

            return { controlsDescendantBindings: true };
            
         update: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->

            # Next, whether or not the supplied model property is observable, get its current value
            valueUnwrapped = ko.utils.unwrapObservable(valueAccessor());
            if valueUnwrapped.template == undefined
              return {controlsDescendantBindings: true};

            templateUnwrapped = ko.utils.unwrapObservable(valueUnwrapped.template);
            $(element).html(templateUnwrapped);

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
                start: (event, eventUI) ->
                  viewModel.isSizeHighlightEnabled(false);
                stop: (event, eventUI) ->
                  #TODO add positionX and positionY for zones
                  viewModel.positionX((eventUI.position.left / viewModel.containerWidthRatio()) + (viewModel.widgetWidth() / 2));
                  viewModel.positionY((eventUI.position.top / viewModel.containerHeightRatio())  + (viewModel.widgetHeight() / 2));
                  viewModel.model().save();
                  viewModel.isSizeHighlightEnabled(true);
            });

            return { controlsDescendantBindings: false };
    };

    ko.bindingHandlers.jquerySelectable = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            $(element).selectable( {
                selected: (event, eventUI) ->
                  if (viewModel.isHighlighted())
                    viewModel.removeHighlight();
                    viewModel.isSelected(false);
                  else
                    viewModel.addHighlight();
                    viewModel.isSelected(true);
                unselected: (event, eventUI) ->
                  viewModel.removeHighlight();
                  viewModel.isSelected(false);
            });

            return { controlsDescendantBindings: false };
    };

    ko.bindingHandlers.jqueryDialog = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            titleUnwrapped = ko.utils.unwrapObservable(valueAccessor());

            $(element).dialog({
                autoOpen: false,
                title: titleUnwrapped,
                minWidth: 300,
                width: "auto"
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

    class NamedViewModel extends kb.ViewModel
        constructor: (model) ->
           super(model);
           @id = kb.observable(model, 'id');
           @name = kb.observable(model, 'name');
           @saveChanges= () =>
              @.model().saveChanges();


    class ScriptViewModel extends NamedViewModel
        constructor: (model) ->
           super(model)
           @state = kb.observable(model, 'state');


    class PositionedImageViewModel extends NamedViewModel
        constructor: (model) ->
           super(model);
           @imgSrc = kb.observable(model, 'imgSrc');
           @positionX = kb.observable(model, 'positionX');
           @positionY = kb.observable(model, 'positionY');
           @width = kb.defaultObservable(kb.observable(model, 'width'), 0);
           @height = kb.defaultObservable(kb.observable(model, 'height'), 0);
           @sizeFactor=ko.observable(1.0);
           @widgetWidth = ko.computed({
              read: () =>
                effWidth = @width() * @sizeFactor();
                return effWidth;
              owner: @
           }
           , @);
           @widgetHeight = ko.computed({
              read: () =>
                effHeight = @height() * @sizeFactor();
                return effHeight;
              owner: @
           }
           , @);
           @styleWidth = ko.computed({
              read: () =>
                return @widgetWidth() + "px";
              owner: @
           }
           , @);
           @styleHeight = ko.computed({
              read: () =>
                return @widgetHeight() + "px";
              owner: @
           }
           , @);
           @containerWidthRatio = ko.observable(1.0);
           @containerHeightRatio = ko.observable(1.0);


    class DecoratorViewModel extends PositionedImageViewModel
        constructor: (model) ->
           super(model);
           @id(model.name);
           @name = kb.defaultObservable(kb.observable(model, 'name'), 'state');

           @show = kb.defaultObservable(kb.observable(model, 'show'), false);
           @imgSrc = kb.defaultObservable(@imgSrc, '/assets/images/devices/decorators/play.png');
           @positionX = kb.defaultObservable(@positionX, 16);
           @positionY = kb.defaultObservable(@positionY, 16);
           if (@width() <= 0)
              @width(15);
           if (@height() <= 0)
              @height(15);
           @containerSizeDelta=ko.observable(0);
           @styleLeft = ko.computed({
              read: () =>
                if ((@sizeFactor() > 1.0) && ((@widgetWidth() - @width()) >= @containerSizeDelta()))
                  effPostionX = @positionX();
                else
                  effPositionX = @positionX() + (@containerSizeDelta() / 2) - ((@widgetWidth() - @width()) / 2);
                return effPositionX + "px";
              owner: @
           }
           , @);
           @styleTop = ko.computed({
              read: () =>
                if ((@sizeFactor() > 1.0) && ((@widgetHeight() - @height()) >= @containerSizeDelta()))
                  effPositionY = @positionY();
                else
                  effPositionY = @positionY() + (@containerSizeDelta() / 2)- ((@widgetHeight() - @height()) / 2);
                return effPositionY + "px";
              owner: @
           }
           , @);


    class DeviceTypeViewModel extends NamedViewModel
        constructor: (model) ->
           super(model);


    class DraggableStateWidgetViewModel extends PositionedImageViewModel
        constructor: (model) ->
           super(model);

           @styleLeft = ko.computed({
              read: () =>
                effPositionX = (@positionX() * @containerWidthRatio()) - (@widgetWidth() / 2);
                return effPositionX + "px";
              owner: @
           }
           , @);
           @styleTop = ko.computed({
              read: () =>
                effPositionY = (@positionY() * @containerHeightRatio()) - (@widgetHeight() / 2);
                return effPositionY + "px";
              owner: @
           }
           , @);
           @tooltipContent = ko.computed( () =>
              return @name() + " /n" + @id();
           , @);
           @statusWindowTitle = ko.computed( () =>
              return @name();
           , @);
           @isSelected = ko.observable(false);
           @isSizeHighlightEnabled = ko.observable(true);
           @isHighlighted = ko.observable(false);
           @addHighlight= () =>
              @isHighlighted(true);
              @updateSize(@isSizeHighlightEnabled());
           @removeHighlight= () =>
              @isHighlighted(false);
              @updateSize(@isSizeHighlightEnabled());
           @updateSize= (isSizeHighlightEnabledVal) =>
              if ((isSizeHighlightEnabledVal && @isHighlighted()) || @isSelected())
                newFactor = 1.2;
              else
                newFactor = 1.0;
              @sizeFactor(newFactor);
              if (@decorators != undefined)
                ko.utils.arrayForEach(@decorators(), (decorator) =>
                  decorator.sizeFactor(newFactor);
                  containerSizeDelta = 0;
                  if (newFactor != 1.0)
                    containerSizeDelta = @width() * (newFactor - 1.0);
                  decorator.containerSizeDelta(containerSizeDelta);
                );

           @isSizeHighlightEnabled.subscribe(@updateSize);
           
           @updateSelected=()=>
              if (@isSelected())
                  @addHighlight();
              else
                  @removeHighlight();
           @selectedSubscription = @isSelected.subscribe(@updateSelected)

           # status window management
           @statusWindowTemplate = ko.observable("");
           @statusWindowVisible = ko.observable(false);


    class ZoneViewModel extends DraggableStateWidgetViewModel
      constructor: (model) ->
        super(model)
        @isRoom = kb.observable(model, 'isRoom');
        @leftX = kb.observable(model, 'leftX');
        @rightX = kb.observable(model, 'rightX');
        @bottomY = kb.observable(model, 'bottomY');
        @topY = kb.observable(model, 'topY');
        @width(@rightX() - @leftX());
        @height(@bottomY() - @topY());
        @variables=kb.observable(model, 'variables');

        @variables_name = ko.computed({
          read: () =>
            if (@.variables() instanceof Object)
              return Object.keys(@.variables());
            else
              return [];
          owner: @
        }
        , @);
        @positionX = ko.computed({
          read: () =>
            return (@leftX() + @rightX()) / 2;
          owner: @
        } , @);
        @positionY = ko.computed({
          read: () =>
            return (@bottomY() + @topY()) / 2;
          owner: @
        } , @);
        @visibility = ko.computed({
          read:()=>
            if (@isSelected())
              # Thomas, help with this line to no update div size!!
              @sizeFactor(1.0);
              return "visible";
            else
              return "hidden";
          }
          , @);

        @background = @.generateBackgroundColor();

        @statusWindowTemplate(zoneStatusWindowTemplateHtml);
      getVariableValue:(variable)->
        return @.variables()[variable]+"";
      generateBackgroundColor:()->
        return "#"+((1<<24)*Math.random()|0).toString(16);

    

    class DeviceViewModel extends DraggableStateWidgetViewModel
        constructor: (model) ->
           super(model);

           @type = kb.observable(model, 'type');
           @location = kb.observable(model, 'location');
           @properties=kb.observable(model, 'properties');
           @properties_name = ko.computed({
            read: () =>
              if (@.properties() instanceof Object)
                return Object.keys(@.properties());
              else
                return [];
            owner: @
           }
           , @);
           @state = kb.defaultObservable(kb.observable(model, 'state'), 'activated');
           @isDesactivated = ko.computed({
              read: () =>
                return @state() == "deactivated";
              owner: @
           }
           , @);
           @fault = kb.defaultObservable(kb.observable(model, 'fault'), 'no');
           @positionX = kb.defaultObservable(@positionX, 0);
           @positionY = kb.defaultObservable(@positionY, 0);
           @width(32);
           @height(32);
           @zones = kb.collectionObservable(DataModel.collections.zones, {view_model: ZoneViewModel});
           @locationZone = ko.computed({
              read: () =>
                zoneModel = @zones.collection().get(@location());
                if (zoneModel == undefined)
                  return null;
                return @zones.viewModelByModel(zoneModel);
              owner: @
           }
           );
           @statusWindowTemplate(deviceStatusWindowTemplateHtml);
           @imgSrc = ko.observable(@.getImage())
           @decorators = ko.observableArray([
                new DecoratorViewModel new Backbone.Model {
                    name: "event",
                    imgSrc: '/assets/images/devices/decorators/event.png',
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
                );

           # location change saving
           @saveLocation= ko.observable(false);
           @saveLocationChanges= (data, event) =>
                @location('bedroom');
                @saveChanges();

           # init
           @updateProperties= (newValue) =>
                if (@type() == "iCASA.BathroomScale" )
                  presence = @properties()["presence_detected"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "foots")
                          decorator.show(presence == true);
                  );
                if (@type() == "iCASA.Sphygmometer" )
                  presence = @properties()["presence_detected"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "sphygmometer_measure")
                       if (presence == true)
                         decorator.show(presence == true);
                  );
                if (@type() == "iCASA.PresenceSensor" )
                  presence = @properties()["presencesensor.sensedpresence"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "presence")
                       decorator.show(presence == true);
                  );
                if (@type() == "iCASA.BinaryLight")
                  powerLevel = @properties()["light.powerStatus"]
                  if (powerLevel)
                    @imgSrc(@getImage("binaryLight_on"));
                  else
                    @imgSrc(@.getImage());
          
           @initDeviceImages= () =>
                if (@type() == "iCASA.BathroomScale")
                     @decorators.push(new DecoratorViewModel new Backbone.Model {
                       name: "foots",
                       imgSrc: '/assets/images/devices/decorators/foots.png',
                       width: 32,
                       height: 32,
                       positionX: 1,
                       positionY: 1,
                       show: false
                     });
                if (@type() == "iCASA.Sphygmometer")
                     @decorators.push(new DecoratorViewModel new Backbone.Model {
                       name: "sphygmometer_measure",
                       imgSrc: '/assets/images/devices/decorators/sphygmometer_measure.png',
                       width: 12,
                       height: 9,
                       positionX: 17,
                       positionY: 4,
                       show: false
                     });
                if (@type() == "iCASA.PresenceSensor")
                     @decorators.push(new DecoratorViewModel new Backbone.Model {
                       name: "presence",
                       imgSrc: '/assets/images/devices/decorators/movementDetector_detected.png',
                       width: 32,
                       height: 32,
                       positionX: 1,
                       positionY: 1,
                       show: false
                     });
                @properties.subscribe(@updateProperties);
                @updateProperties();

           @initDeviceImages();
           
           @state.subscribe(@updateWidgetImg);
           @fault.subscribe(@updateWidgetImg);
           @updateWidgetImg();
        
        getPropertyValue:(property)->
          return @.properties()[property]+""
        getImage:(imgName)->
          if not imgName?
            imgName = "genericDevice";
            if (@type() == "iCASA.Cooler")
              imgName = "cooler";
            if (@type() == "iCASA.AudioSource")
              imgName = "musicPlayer";
            if (@type() == "iCASA.DimmerLight")
              imgName = "dimmerLight";
            if (@type() == "iCASA.Thermometer")
              imgName = "thermometer";
            if (@type() == "iCASA.MedicalThermometer")
              imgName = "medicalThermometer";
            if (@type() == "iCASA.Heater")
              imgName = "heater";
            if (@type() == "iCASA.Photometer")
              imgName = "photometer";
            if (@type() == "iCASA.BinaryLight")
              imgName = "binaryLight_off";
            if (@type() == "iCASA.PresenceSensor")
              imgName = "movementDetector";
            if (@type() == "iCASA.Speaker")
              imgName = "speaker";
            if (@type() == "iCASA.Power")
              imgName = "power";
            if (@type() == "iCASA.BathroomScale")
              imgName = "bathroomScale";
            if (@type() == "iCASA.Tablet")
              imgName = "tablet";
            if (@type() == "iCASA.Desktop")
              imgName = "desktop";
            if (@type() == "iCASA.SettopBox")
              imgName = "liveBox";
            if (@type() == "iCASA.RollingShutter")
              imgName = "rollingShutter";
            if (@type() == "iCASA.LiquidDetector")
              imgName = "liquidDetector";
            if (@type() == "iCASA.SmartPhone")
              imgName = "smartPhone";
            if (@type() == "iCASA.FlatTV")
              imgName = "flatTV";
            if (@type() == "iCASA.RFIDReader")
              imgName = "rfidReader";
            if (@type() == "iCASA.Accelerometer")
              imgName = "accelerometer";
            if (@type() == "iCASA.ToggleSwitch")
              imgName = "toggleSwitch";
            if (@type() == "iCASA.DoorDetector")
              imgName = "doorDetector";
            if (@type() == "iCASA.SettopBox")
              imgName = "liveBox";
            if (@type() == "iCASA.Sphygmometer")
              imgName = "sphygmometer";
          return "/assets/images/devices/" + imgName + ".png"; 

    class PersonTypeViewModel extends NamedViewModel
        constructor: (model) ->
           super(model);


    class PersonViewModel extends DraggableStateWidgetViewModel
        constructor: (model) ->
           super(model);

           @location = kb.observable(model, 'location');
           @positionX = kb.defaultObservable(@positionX, 0);
           @positionY = kb.defaultObservable(@positionY, 0);
           @width(50);
           @height(50);
           @zones = kb.collectionObservable(DataModel.collections.zones, {view_model: ZoneViewModel});
           @locationZone = ko.computed({
              read: () =>
                zoneModel = @zones.collection().get(@location());
                if (zoneModel == undefined)
                  return null;
                return @zones.viewModelByModel(zoneModel);
              owner: @
           }
           );
           @type = kb.observable(model, 'type');
           @personTypes = kb.collectionObservable(DataModel.collections.personTypes, {view_model: PersonTypeViewModel});
           @typeObj = ko.computed({
              read: () =>
                typeModel = @personTypes.collection().get(@type());
                if (typeModel == undefined)
                  return null;
                return @personTypes.viewModelByModel(typeModel);
              owner: @
           }
           );
           @statusWindowTemplate(personStatusWindowTemplateHtml);
           @imgSrc = ko.computed(() =>
              imgName = "user2";
              if (@type() == "Grandmother")
                imgName = "user1";
              if (@type() == "Grandfather")
                imgName = "user2";
              if (@type() == "Father")
                imgName = "user3";
              if (@type() == "Mother")
                imgName = "user4";
              if (@type() == "Girl")
                imgName = "user5";
              if (@type() == "Boy")
                imgName = "user6";
              if (@type() == "Sherlock")
                imgName = "user7";

              return "/assets/images/users/" + imgName + ".png";
           , @);
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

           @imgSrc = ko.observable(model.imgSrc);
           @mapWidth = ko.observable(0);
           @mapHeight = ko.observable(0);
           @computeMapImgSize = () =>
             imgSrcNoCache = @imgSrc() + '?cache=' + Date.now();
             iCasaViewModel = @;
             $('<img/>').attr('src', imgSrcNoCache).load(() ->
               iCasaViewModel.mapWidth(this.width);
               iCasaViewModel.mapHeight(this.height);
             );
           @computeMapImgSize();
           @mapWidthRatio = ko.observable(1.0);
           @mapHeightRatio = ko.observable(1.0);
           @updateMapSize = () =>
              mapElt = $("#mapImg")
              mapEffWidth = mapElt.width();
              mapEffHeight = mapElt.height();
              if (@mapWidth() > 0)
                @mapWidthRatio(mapEffWidth / @mapWidth());
              if (@mapHeight() > 0)
                @mapHeightRatio(mapEffHeight / @mapHeight());

           @deviceTypes = kb.collectionObservable(DataModel.collections.deviceTypes, {view_model: DeviceTypeViewModel});

           @devices = kb.collectionObservable(DataModel.collections.devices, {view_model: DeviceViewModel} );
           

           @persons = kb.collectionObservable(DataModel.collections.persons, {view_model: PersonViewModel});

           @personTypes = kb.collectionObservable(DataModel.collections.personTypes, {view_model: PersonTypeViewModel});

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

           @scripts = kb.collectionObservable(DataModel.collections.scripts, {view_model: ScriptViewModel});

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


           # device management

           @newDeviceType = ko.observable("");

#           @deviceTypes.subscribe(@.selectFirstDeviceType)
#           selectFirstDeviceType: (models) =>
#              if models.length > 0
#                firstModel = models[0]
#                this.newDeviceType(firstModel)



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
              generatedId = @.createRandomId(DataModel.collections.devices, @newDeviceType().name());
              newDevice = new DataModel.Models.Device({ deviceId: generatedId, name: @newDeviceName(), "type": @newDeviceType().name(), positionX: 1, positionY: 1, properties: {}});
              newDevice.save();
              newDevice.set(id: generatedId)
              DataModel.collections.devices.push(newDevice);

           @removeDevice = (device) =>
              device.model().destroy();

           @removeSelectedDevices = () =>
             toRemoveModels = []
             ko.utils.arrayForEach(@devices(), (device) =>
               if (device == undefined)
                 return;

               if (device.isSelected())
                  toRemoveModels.push device.model()
             );
             for toRemoveModel in toRemoveModels
                toRemoveModel.destroy()

           @showDeviceWindow = (device) =>
              device.statusWindowVisible(false);
              device.statusWindowVisible(true);


           # person management

           @newPersonName = ko.observable("");

           @newPersonType = ko.observable("Father");

           @createPerson = () =>
              newPerson = new DataModel.Models.Person({ personId: @newPersonName(), name: @newPersonName(), "type": @newPersonType().name(), positionX: 1, positionY: 1 });
              newPerson.save();
              newPerson.set(id: @newPersonName())
              DataModel.collections.persons.push(newPerson);

              

           @removeSelectedPersons = () =>
              ko.utils.arrayForEach(@persons(), (person) =>
                if (person == undefined)
                  return;

                if (person.isSelected())
                  person.model().destroy();
              );

           @removePerson = (person) =>
              person.model().destroy();

           @showPersonWindow = (person) =>
              person.statusWindowVisible(false);
              person.statusWindowVisible(true);


           # zone management

           @newZoneName = ko.observable("");

           @createZone = () =>
              newZone = new DataModel.Models.Zone({ zoneId: @newZoneName(), name: @newZoneName(), isRoom: false, leftX: 1, topY: 1, rightX : 21, bottomY: 21 });
              newZone.save();
              newZone.set(id: @newZoneName())
              DataModel.collections.zones.push(newZone);
              
              
           @removeSelectedZones = () =>
              ko.utils.arrayForEach(@zones(), (zone) =>
                if (zone == undefined)
                  return;

                if (zone.isSelected())
                  zone.model().destroy();
              );

           @removeZone = (zone) =>
              zone.model().destroy();

           @showZoneWindow = (zone) =>
              zone.statusWindowVisible(false);
              zone.statusWindowVisible(true);

           # script management

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

           # managing map size change (must update position of persons, zones and devices)
           @updateModelPosition=(model)=>
             model.containerWidthRatio(@mapWidthRatio());
             model.containerHeightRatio(@mapHeightRatio());

           @updateWidgetPositions= (newValue) =>
             #TODO should use a merged list of positioned objects
             ko.utils.arrayForEach(@devices(), (device) =>
              @updateModelPosition(device);
             );
             ko.utils.arrayForEach(@persons(), (person) =>
              @updateModelPosition(person);
             );
             ko.utils.arrayForEach(@zones(), (zone) =>
               @updateModelPosition(zone);
             );
           @mapWidthRatio.subscribe(@updateWidgetPositions);
           @mapHeightRatio.subscribe(@updateWidgetPositions);

           @.persons.subscribe(@.updateWidgetPositions)
           @.devices.subscribe(@.updateWidgetPositions)
           @.zones.subscribe(@.updateWidgetPositions)
        
        createRandomId:(collection, type)->
           number = Math.floor((Math.random()*Number.MAX_VALUE)+1); 
           hexaNumner = number.toString(16).substr(0,10);
           nid = type.replace("iCASA.", "") + "-" + hexaNumner;
           testExistance = collection.get(nid);
           if (testExistance != undefined && testExistance != null)
              return createRandomId(collection, type);
           return nid;



    return ICasaViewModel;
);
