
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

    ko.bindingHandlers.jqueryResizable = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            # This will be called when the binding is first applied to an element

            $(element).resizable( {
                start: (event, eventUI) ->
                  viewModel.isSizeHighlightEnabled(false);
                stop: (event, eventUI) ->
                  #TODO add positionX and positionY for zones
                  rightX = eventUI.size.width / viewModel.containerWidthRatio() + viewModel.leftX();
                  bottomY = eventUI.size.height / viewModel.containerHeightRatio() + viewModel.topY();
                  width = rightX - viewModel.leftX();
                  height = bottomY - viewModel.topY();
                  viewModel.width(width);
                  viewModel.height(height);
                  viewModel.positionX(viewModel.leftX() + width / 2);
                  viewModel.positionY(viewModel.topY() + height / 2);
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

    ko.bindingHandlers.sortable = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->

            asc = $('<i></i>').addClass("icon-chevron-down pull-right");
            desc = $('<i></i>').addClass("icon-chevron-up pull-right").hide();
            asc.click(()->
                desc.show()
                asc.hide()
                viewModel.sortFields(1, valueAccessor().obj, valueAccessor().column)
            )
            desc.click(()->
                asc.show()
                desc.hide()
                viewModel.sortFields(-1, valueAccessor().obj, valueAccessor().column)
            )
            $(element).append(asc);
            $(element).append(desc);

            return { controlsDescendantBindings: false };
    };

    ko.bindingHandlers.jqFactorSlider = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->
            options = allBindingsAccessor().sliderOpt || {}
            sliderValue = ko.utils.unwrapObservable(valueAccessor());

            options.slide = (e, ui) ->
                sliderValue.value(ui.value)

            options.stop = (e, ui) ->
                viewModel.clock.model().save();

            ko.utils.domNodeDisposal.addDisposeCallback(element, () ->
              $(element).slider("destroy")
            )
            $(element).slider(options)
        update: (element, valueAccessor) ->
            sliderValue = ko.toJS(valueAccessor())
            $(element).slider("value", sliderValue.value);

    }

    ko.bindingHandlers.jqScriptProgress = {

        init: (element, valueAccessor, allBindingsAccessor, viewModel, bindingContext) ->

            label = $("<div></div>").text("0%").width("100%").css("text-align", "center").addClass("pull-left")
            $(element).append(label)
            $(element).progressbar({
              value: valueAccessor().value,
              max: 100,
              change: () ->
                  label.text( $(element).progressbar( "value" ).toFixed(2) + "%" );
              complete: () ->
                label.text( "Complete" );
                viewModel.selectedScript().state('stopped');
            })

        update: (element, valueAccessor) ->
            valueProgress = ko.toJS(valueAccessor())
            $(element).progressbar("value", valueProgress.value);
    }

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
                  effPositionX = @positionX() + (@containerSizeDelta() / 2) - ((@widgetWidth() - @width()) / 2);
                else
                  effPositionX = @positionX();
                return effPositionX + "px";
              owner: @
           }
           , @);
           @styleTop = ko.computed({
              read: () =>
                if ((@sizeFactor() > 1.0) && ((@widgetHeight() - @height()) >= @containerSizeDelta()))
                  effPositionY = @positionY() + (@containerSizeDelta() / 2)- ((@widgetHeight() - @height()) / 2);
                else
                  effPositionY = @positionY();
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
        @variables = kb.observable(model, 'variables');
        if model.get('isSelected')?
          @isSelected(model, 'isSelected');
        @width(@rightX() - @leftX());
        @height(@bottomY() - @topY());
        @positionX((@leftX() + @rightX()) / 2);
        @positionY((@bottomY() + @topY()) / 2);
        @variables_name = ko.computed({
          read: () =>
            if (@.variables() instanceof Object)
              return Object.keys(@.variables());
            else
              return [];
          owner: @
        }
        , @);

        # To override the resize event when selected zone.
        @sizeFactor = ko.computed({
          read: () =>
            return 1.0;
          write: (newValue)=>
            return ;
          }, @);

        # @updateWidth = (newValue)=>
        #   @width(@rightX() - @leftX());
        # @updateHeight = (newValue)=>
        #   @height(@bottomY() - @topY());

        # @rightX.subscribe(@updateWidth);
        # @leftX.subscribe(@updateWidth);
        # @bottomY.subscribe(@updateHeight);
        # @topY.subscribe(@updateHeight);

        @positionX.subscribe((value)=>
            @rightX(value + @width()/2);
            @leftX(value - @width()/2);
          )

        @positionY.subscribe((value)=>
            @topY(value - @height()/2);
            @bottomY(value + @height()/2);
          )

        @visibility = ko.computed({
          read:()=>
            if (@isSelected())
              return "visible";
            else
              return "hidden";
          }
          , @);
        @styleLeft = ko.computed({
              read: () =>
                effPositionX = (@positionX() * @containerWidthRatio()) - (@width() * @containerWidthRatio() / 2);
                return effPositionX + "px";
              owner: @
          }
          , @);
        @styleTop = ko.computed({
              read: () =>
                effPositionY = (@positionY() * @containerHeightRatio()) - (@height() * @containerHeightRatio() / 2);
                return effPositionY + "px";
              owner: @
          }
          , @);
        @styleWidth = ko.computed({
              read: () =>
                effWidth = @width() * @containerWidthRatio();
                return effWidth + "px";
              owner: @
          }
          , @);
        @styleHeight = ko.computed({
              read: () =>
                effHeight = @height() * @containerHeightRatio();
                return effHeight + "px";
              owner: @
          }
          ,@);
        @background = @.generateBackgroundColor();
        @borderColor = @background;
        @statusWindowTemplate(zoneStatusWindowTemplateHtml);
      getVariableValue:(variable)->
        return @.variables()[variable]+"";
      generateBackgroundColor:()->
        r = Math.floor(Math.random() * 256);
        g = Math.floor(Math.random() * 256);
        b = Math.floor(Math.random() * 256);
        return "rgba("+r+","+g+","+b+", 0.4)";

    

    class DeviceViewModel extends DraggableStateWidgetViewModel
        constructor: (model) ->
           super(model);

           @type = kb.observable(model, 'type');
           @location = kb.observable(model, 'location');
           @services = kb.observable(model, 'services');
           @hasService = (service) =>
            curServices = @.services();
            if (!curServices)
              return false;
            foundService = ko.utils.arrayFirst(curServices, (curService) ->
              return ko.utils.stringStartsWith(curService, service);
            );
            if (foundService)
              return true;
            else
              return false;
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
                @location('bedroom'); #TODO manage location property change
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
                if ((@type() == "iCASA.PresenceSensor") || @hasService("fr.liglab.adele.icasa.device.presence.PresenceSensor"))
                  presence = @properties()["presenceSensor.sensedPresence"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "presence")
                       decorator.show(presence == true);
                  );
                if ((@type() == "iCASA.BinaryLight") || @hasService("fr.liglab.adele.icasa.device.light.BinaryLight"))
                  powerLevel = @properties()["binaryLight.powerStatus"]
                  if (powerLevel)
                    @imgSrc(@getImage("binaryLight_on"));
                  else
                    @imgSrc(@.getImage());
                if ((@type() == "iCASA.COGasSensor") || @hasService("fr.liglab.adele.icasa.device.gasSensor.CarbonMonoxydeSensor"))
                  concentration = @properties()["carbonMonoxydeSensor.currentConcentration"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "redLed")
                       decorator.show(concentration >= 2.0);
                  );
                if ((@type() == "iCASA.CO2GasSensor") || @hasService("fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor"))
                  concentration = @properties()["carbonDioxydeSensor.currentConcentration"];
                  ko.utils.arrayForEach(@decorators(), (decorator) ->
                     if (decorator.name() == "redLed")
                       decorator.show(concentration >= 2.0);
                  );
          
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
                if ((@type() == "iCASA.COGasSensor") || (@type() == "iCASA.CO2GasSensor"))
                     @decorators.push(new DecoratorViewModel new Backbone.Model {
                       name: "redLed",
                       imgSrc: '/assets/images/devices/decorators/redLed.png',
                       width: 8,
                       height: 8,
                       positionX: 4,
                       positionY: 4,
                       show: false
                     });
                     ko.utils.arrayForEach(@decorators(), (decorator) ->
                       if ((decorator.name() == "activated") || (decorator.name() == "fault"))
                         decorator.positionY(1);
                         decorator.positionX(0);
                     );
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
           @popoverdata = ()=>
             pop = "Name : "+@.name()
             for property in @properties_name()
               pop+= "<br/>"+property+" : "+@getPropertyValue(property);
             return pop;
        
        getPropertyValue:(property)->
          return @.properties()[property]+""
        getImage:(imgName)->
          if not imgName?
            imgName = "genericDevice";
            if ((@type() == "iCASA.Cooler") || @hasService("fr.liglab.adele.icasa.device.temperature.Cooler"))
              imgName = "cooler";
            if ((@type() == "iCASA.AudioSource") || @hasService("fr.liglab.adele.icasa.device.sound.AudioSource"))
              imgName = "musicPlayer";
            if ((@type() == "iCASA.BinaryLight") || @hasService("fr.liglab.adele.icasa.device.light.BinaryLight"))
              imgName = "binaryLight_off";
            if ((@type() == "iCASA.DimmerLight") || @hasService("fr.liglab.adele.icasa.device.light.DimmerLight"))
              imgName = "dimmerLight";
            if ((@type() == "iCASA.Thermometer") || @hasService("fr.liglab.adele.icasa.device.temperature.Thermometer"))
              imgName = "thermometer";
            if ((@type() == "iCASA.MedicalThermometer") || @hasService("fr.liglab.adele.icasa.device.bathroomscale.MedicalThermometer"))
              imgName = "medicalThermometer";
            if ((@type() == "iCASA.Heater") || @hasService("fr.liglab.adele.icasa.device.temperature.Heater"))
              imgName = "heater";
            if ((@type() == "iCASA.Photometer") || @hasService("fr.liglab.adele.icasa.device.light.Photometer"))
              imgName = "photometer";
            if ((@type() == "iCASA.COGasSensor") || @hasService("fr.liglab.adele.icasa.device.gasSensor.CarbonMonoxydeSensor"))
              imgName = "COGazSensor";
            if ((@type() == "iCASA.CO2GasSensor") || @hasService("fr.liglab.adele.icasa.device.gasSensor.CarbonDioxydeSensor"))
              imgName = "CO2GazSensor";
            if ((@type() == "iCASA.PresenceSensor") || @hasService("fr.liglab.adele.icasa.device.presence.PresenceSensor"))
              imgName = "movementDetector";
            if ((@type() == "iCASA.Speaker") || @hasService("fr.liglab.adele.icasa.device.sound.Speaker"))
              imgName = "speaker";
            if (@type() == "iCASA.Power")
              imgName = "power";
            if ((@type() == "iCASA.BathroomScale") || @hasService("fr.liglab.adele.icasa.device.bathroomscale.BathroomScale"))
              imgName = "bathroomScale";
            if (@type() == "iCASA.Tablet")
              imgName = "tablet";
            if (@type() == "iCASA.Desktop")
              imgName = "desktop";
            if ((@type() == "iCASA.SettopBox") || @hasService("fr.liglab.adele.icasa.device.settopbox.SetTopBox"))
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
            if ((@type() == "iCASA.ToggleSwitch") || @hasService("fr.liglab.adele.icasa.device.power.PowerSwitch"))
              imgName = "toggleSwitch";
            if (@type() == "iCASA.DoorDetector")
              imgName = "doorDetector";
            if (@type() == "iCASA.SettopBox")
              imgName = "liveBox"; #override settopbox icon
            if ((@type() == "iCASA.Sphygmometer") || @hasService("fr.liglab.adele.icasa.device.bathroomscale.Sphygmometer"))
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

    class ClockViewModel extends kb.ViewModel
        constructor: (@model) ->
          super(model);

          @currentTime = kb.observable(model, 'currentTime');
          @startDate = kb.observable(model, 'startDate');
          @pause = kb.observable(model, 'pause');
          @factor = kb.observable(model, 'factor');
          @factorTmp = kb.observable(model,'factor');
          @factorEditing = ko.observable(false);
          @updateFactor = ()=>
                if !@factorEditing() && @factorTmp() != @.model().previous('factor')
                    @.model().save();

          @factorEditing.subscribe(@updateFactor);
          @minutes = ko.computed({
             read: =>
               d = new Date(@currentTime())
               return d.getMinutes()

             write: (val)=>
               d = new Date(@currentTime())
               d.setMinutes(val)
               @currentTime(d.getTime())
          }, @)

          @hours = ko.computed({
             read: =>
               d = new Date(@currentTime())
               return d.getHours()

             write: (val)=>
               d = new Date(@currentTime())
               d.setHours(val)
               @currentTime(d.getTime())
          }, @)

          @date = ko.computed({
             read: =>
               d = new Date(@currentTime())
               day = if d.getDate() < 10 then '0'+ d.getDate() else d.getDate()
               month = if d.getMonth() < 9 then '0'+ (d.getMonth()+1) else (d.getMonth()+1)
               return day + "/" + month + "/" + d.getFullYear();

             write: (val)=>
               d = new Date(@currentTime())
               d.setFullYear(val.substring(6,10))
               d.setMonth(val.substring(3,5)-1)
               d.setDate(val.substring(0,2))
               @currentTime(d.getTime())
          }, @)



    class ICasaViewModel extends kb.ViewModel
        constructor : (model) ->
           #backend and frontend information
           @backendVersion = kb.observable(DataModel.models.backend, 'version');
           @frontendVersion = kb.observable(DataModel.models.frontend, 'version');

           @sameVersion = ko.computed( () =>
                sv = @frontendVersion() == @backendVersion();
                if sv == false
                    $("#compatibilityWarn").removeClass("hidden");
                else
                    $("#compatibilityWarn").addClass("hidden");
                return sv;
           , @);

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

           @clock = new ClockViewModel(DataModel.models.clock);

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

           @deviceFilter = ko.observable("");
           @zoneFilter = ko.observable("");
           @personFilter = ko.observable("");

           @sortFields = (order, list, property) =>
             @[list+"s"]().sort((a,b) ->
               if a[property]() > b[property]()
                 return 1*order
               else if b[property]() > a[property]()
                 return -1*order
               else
                 return 0
             )
             tps = @[list+"Filter"]();
             @[list+"Filter"]("\\");
             @[list+"Filter"](tps);

           @filteredList = (list) =>
              opt = @[list+"Filter"]().split("=");
              if opt.length <= 1
                attr = "name";
                search = opt[0];
              else
                attr = opt[0];
                search = opt[1];

              try
                filter = new RegExp(search, "i");
              catch err
                console.log("Bad regexp : "+err.message);
                filter = new RegExp("", "i")
              return ko.utils.arrayFilter( @[list+"s"](), (element) ->
                try
                  if attr == "name" or attr == "state" or attr == "location" or attr == "falt" or attr == "id"
                    return filter.test( element[attr]() )
                  else
                    return filter.test( element.getPropertyValue(attr) )
                catch err
                  return false
              )

           @filteredDevices = ko.computed(() =>
              return @filteredList("device")
           , @)

           @filteredZones = ko.computed(() =>
              return @filteredList("zone")
           , @)

           @filteredPersons = ko.computed(() =>
              return @filteredList("person")
           , @)

           @checkAllDevices = ko.computed({
              read: =>
                selected = 0
                for device in @devices()
                  if device.isSelected()
                    selected++
                return @devices().length == selected

              write: (val) =>
                for device in @devices()
                  device.isSelected(val)
           }, @)

           # person management

           @newPersonName = ko.observable("");

           @newPersonType = ko.observable("Father");

           @createPerson = () =>
              newPerson = new DataModel.Models.Person({ personId: @newPersonName(), name: @newPersonName(), "type": @newPersonType().name(), positionX: 1, positionY: 1 });
              newPerson.save();
              newPerson.set(id: @newPersonName())
              DataModel.collections.persons.push(newPerson);
              @newPersonName("");

              

           @removeSelectedPersons = () =>
              toRemoveModels = []
              ko.utils.arrayForEach(@persons(), (person) =>
                if (person == undefined)
                  return;

                if (person.isSelected())
                  toRemoveModels.push person.model()
              );
              for toRemoveModel in toRemoveModels
                toRemoveModel.destroy()

           @removePerson = (person) =>
              person.model().destroy();

           @showPersonWindow = (person) =>
              person.statusWindowVisible(false);
              person.statusWindowVisible(true);

           @checkAllPersons = ko.computed({
             read: =>
               selected = 0
               for person in @persons()
                 if person.isSelected()
                   selected++
               return @persons().length == selected
             write: (val) =>
               for person in @persons()
                 person.isSelected(val)
           }, @)

           # zone management

           @newZoneName = ko.observable("");

           @createZone = () =>
              newZone = new DataModel.Models.Zone({ zoneId: @newZoneName(), name: @newZoneName(), isRoom: false, leftX: 1, topY: 1, rightX : 50, bottomY: 50 , isSelected: true});
              newZone.save();
              newZone.set(id: @newZoneName());
              DataModel.collections.zones.push(newZone);
              
              
           @removeSelectedZones = () =>
              toRemoveModels = []
              ko.utils.arrayForEach(@zones(), (zone) =>
                if (zone == undefined)
                  return;

                if (zone.isSelected())
                  toRemoveModels.push zone.model();
              );
              for toRemoveModel in toRemoveModels
                toRemoveModel.destroy();

           @removeZone = (zone) =>
              zone.model().destroy();

           @showZoneWindow = (zone) =>
              zone.statusWindowVisible(false);
              zone.statusWindowVisible(true);

           @checkAllZones = ko.computed({
             read: =>
               selected = 0
               for zone in @zones()
                 if zone.isSelected()
                   selected++
               return @zones().length == selected
             write: (val) =>
               for zone in @zones()
                 zone.isSelected(val)
           }, @)

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
              if (@selectedScript())
                startDate =  @selectedScript().startDate()
                d = new Date(startDate.substring(6,10),startDate.substring(3,5)-1,startDate.substring(0,2),startDate.substring(11,13),startDate.substring(14,16),startDate.substring(17,19),0)
                @clock.startDate(d.getTime())
                @clock.currentTime(d.getTime())
                @selectedScript().state('started');
                @selectedScript().model().save();

           @stopScript = () =>
              if (@selectedScript())
                @selectedScript().state('stopped');
                @selectedScript().model().save();

           @pauseScript = () =>
              if (@selectedScript())
                @selectedScript().state('paused');
                @selectedScript().model().save();

           @newScriptName = ko.observable("");

           @saveScript = ()=>
              newName = @newScriptName().replace(".bhv", "") + ".bhv";
              newScript = new DataModel.Models.Script({ scriptId: newName, name: newName, state: "stopped"});
              newScript.save();
              newScript.set(id: newName);
              DataModel.collections.scripts.push(newScript);
              @newScriptName("");
           @resetState = ()=>
              DataModel.resetState();

           @scriptStartDate = ko.computed({
              read: =>
                 if (@selectedScript())
                   return @selectedScript().startDate().substring(0,10)
                 else
                   d = new Date()
                   day = if d.getDate() < 10 then '0'+ d.getDate() else d.getDate()
                   month = if d.getMonth() < 9 then '0'+ d.getMonth() else d.getMonth()
                   return day + "/" + month + "/" + d.getFullYear();
              write: (date)=>
                @selectedScript().startDate(date+"-00:00:00")
           }, @)

           @scriptEndDate = ko.computed( () =>
              if @selectedScript()
                dateStr = @selectedScript().startDate()
                d = new Date(dateStr.substring(6,10),dateStr.substring(3,5)-1,dateStr.substring(0,2),dateStr.substring(11,13),dateStr.substring(14,16),dateStr.substring(17,19),0)
                timestamp = d.getTime() + (@selectedScript().executionTime() * 60 * 1000)
                d = new Date(timestamp)
                day = if d.getDate() < 10 then '0'+ d.getDate() else d.getDate()
                return day + "/" + (d.getMonth()+1) + "/" + d.getFullYear();
              else
                d = new Date()
                day = if d.getDate() < 10 then '0'+ d.getDate() else d.getDate()
                return day + "/" + (d.getMonth()+1) + "/" + d.getFullYear();
           );

           # NOT USED for now
           @selectedScriptFactor = ko.computed({
              read: =>
                if (@selectedScript())
                  return @selectedScript().factor()
                else
                  return 1;

              write: (val)=>
                if(@selectedScript())
                  @selectedScript().factor(val)
           }, @)

           @maxFactor = ko.computed( ()=>
             clockFactor = @clock.factor();
             if (clockFactor)
               if (clockFactor > 15000)
                 return clockFactor;
               else
                 return 15000;
             else
               return 15000;
           )

           @scriptProgress = ko.computed( ()=>
              if (@selectedScript())
                if ( (@selectedScript().executionTime() != 0) && ( (@selectedScript().state() == 'started') || (@selectedScript().state() == 'paused')))
                  scriptTime = @clock.currentTime() - @clock.startDate()
                  executionTimeMs = (@selectedScript().executionTime() * 60 * 1000)
                  return (1 - ( (executionTimeMs - scriptTime) / executionTimeMs) ) * 100
                else
                  return 0
              else
                return 0
           );

           @startClockTimer = ()=>
              timer = ()=>
                if (! @clock.pause())
                  currentTime = @clock.currentTime()
                  @clock.currentTime(currentTime + (100 * @clock.factor()))
                  setTimeout(timer, 100)

              timer();

           @pauseClock = ()=>
              @clock.pause(true);
              @clock.model().save();

           @startClock = ()=>
              @clock.pause(false);
              @clock.model().save();

           # init clock state
           @updateClockTimer= (newValue) =>
             clockPaused = newValue;
             if (! clockPaused)
               @startClockTimer();

           @clock.pause.subscribe(@updateClockTimer);
           @startClockTimer();

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
