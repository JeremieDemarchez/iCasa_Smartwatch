# launch application
require([
    'jquery',
    'jquery.ui',
    'knockout',
    'hubu',
    'viewModels/iCasaViewModel',
    'dataModels/ICasaDataModelNotifs',
    'components/ConnectionWidget',
    'components/GatewayConnectionMgrImpl',
    'components/ICasaShellManagerImpl',
    'domReady',
    'jquery.resize'
    ],
    ($, ui, ko, hub, ICasaViewModel, iCasaNotifSocket, ConnectionWidget, GatewayConnectionMgrImpl, ShellManager) ->

        mapRealSizeWidth = 10;
        mapRealSizeHeight = 10;

        class SizeUtil

          @computeMapImgSize = (imgSrc) ->
            imgSrcNoCache = imgSrc #+ '?cache=' + Date.now();
            $('<img/>').attr('src', imgSrcNoCache).load(() ->
              mapRealSizeWidth = this.width;
              mapRealSizeHeight = this.height;
              SizeUtil.initAreaSizes(mapRealSizeWidth, mapRealSizeHeight);
            );

          @getViewportSize = () ->
            e = window;
            a = 'inner';
            if (!('innerWidth' in window))
              a = 'client';
              e = document.documentElement || document.body;
            return { width : e[ a+'Width' ], height : e[ a+'Height' ] };

          @initAreaSizes = (mapWidth, mapHeight) ->
            viewportSize = @.getViewportSize();

            map = $("#map");

            # hypothesys : areaBorderSize = padding + marging + border = 10 + 3 + 15 = 28px
            areaBorderSize = 28;
            actionTabMinWidth = parseInt($('#actionTabs').css('min-width'), 10) ;
            availableWidthFor2Blocks = viewportSize.width - (4 * areaBorderSize) - 25;
            availableWidthFor1Block = viewportSize.width - (2 * areaBorderSize) - 25;

            #calculate width and height to fith in available space
            if viewportSize.width >= 900
              calculatedWidth = availableWidthFor2Blocks - actionTabMinWidth;
              if (calculatedWidth > mapRealSizeWidth)
                calculatedWidth = mapRealSizeWidth;
              calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
              map.width(calculatedWidth);
              map.height(calculatedHeight);
            else
              calculatedWidth = availableWidthFor1Block;
              if (calculatedWidth > mapRealSizeWidth)
                calculatedWidth = mapRealSizeWidth;
              calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
              map.width(availableWidthFor1Block);
              map.height(calculatedHeight);
            @computeAreaSizes("map");

          @computeAreaSizes = (resizedAreaId) ->
            viewportSize = @.getViewportSize();

            # hypothesys : areaBorderSize = padding + marging + border = 10 + 3 + 15 = 28px
            areaBorderSize = 28;
            map = $("#map");
            mapWidth = map.width();
            mapHeight = map.height();
            actionTabs = $("#actionTabs");
            actionTabMinWidth = parseInt($('#actionTabs').css('min-width'), 10) ;
            actionTabsMinHeight = 600;
            actionTabsWidth = actionTabs.width();
            actionTabsHeight = actionTabs.height();
            availableWidthFor2Blocks = viewportSize.width - (4 * areaBorderSize) - 25;
            availableWidthFor1Block = viewportSize.width - (2 * areaBorderSize) - 25;
            if (resizedAreaId == "map")
              if (viewportSize.width >= 900)
                actionTabs.width(availableWidthFor2Blocks - mapWidth);
                calculatedHeight = mapHeight;
                if (calculatedTabHeight < actionTabsMinHeight)
                  calculatedTabHeight = actionTabsMinHeight;
                actionTabs.height(calculatedTabHeight);
              else
                calculatedWidth = availableWidthFor1Block;
                calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
                actionTabs.width(calculatedWidth);
                actionTabs.height(actionTabsMinHeight);
                map.width(calculatedWidth);
                map.height(calculatedHeight);
            else if ((resizedAreaId == undefined) || (resizedAreaId == null))
              if (viewportSize.width >= 900)
                calculatedWidth = availableWidthFor2Blocks - actionTabMinWidth;
                if (calculatedWidth > mapRealSizeWidth)
                  calculatedWidth = mapRealSizeWidth;
                calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
                map.width(calculatedWidth);
                map.height(calculatedHeight);
                calculatedTabHeight = mapHeight;
                if (calculatedTabHeight < actionTabsMinHeight)
                  calculatedTabHeight = actionTabsMinHeight;
                actionTabs.width(availableWidthFor2Blocks - calculatedWidth);
                actionTabs.height(calculatedTabHeight);
              else
                calculatedWidth = availableWidthFor1Block;
                calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
                actionTabs.width(calculatedWidth);
                actionTabs.height(actionTabsMinHeight);
                map.width(calculatedWidth);
                map.height(calculatedHeight);
            else
              calculatedWidth = availableWidthFor2Blocks - actionTabsWidth;
              if (calculatedWidth > mapRealSizeWidth)
                calculatedWidth = mapRealSizeWidth;
              calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;
              map.width(calculatedWidth);
              map.height(calculatedHeight);


            statusWindows = $("#statusWindows");
            statusWindowsWidth = statusWindows.width();
            statusWindows.width(availableWidthFor1Block);
            statusWindowsHeight = statusWindows.height();

            shellWindows = $("#shellContainer");
            shellWindows.width(availableWidthFor1Block);
            #shellFixedWindow = $("#shellOutputFixed");
            #shellFixedWindow.width(viewportSize.width - (3 * areaBorderSize));

        mapName = $("#map").attr("mapId");
        mapImgUrl = $("#map").attr("mapImgSrc");
        servletType = $("#map").attr("servletType");
        mapHeightSubscritionCalls = 0;
        iCasaViewModel = new ICasaViewModel( {
          id: mapName,
          imgSrc: mapImgUrl,
          servletType: servletType
        });
        SizeUtil.computeMapImgSize(mapImgUrl);

        #DO NOT MOVE following instruction, container must be defined resizable before nested resizable elements
        $("#map").resizable({
          animate: true,
          aspectRatio : true,
          ghost: true,
          stop: (event, eventUI) ->
            SizeUtil.computeAreaSizes("map");
        });

        ko.applyBindings(iCasaViewModel);

        $(".slider" ).slider();
        $("#tabs").tabs({
            heightStyle: "fill"
        });

        $("#actionTabs").resizable({
            animate: true,
            aspectRatio : false,
            ghost: true,
            handles: "e, s, se, sw, w",
            stop: (event, eventUI) ->
              SizeUtil.computeAreaSizes("actionTabs");
              $("#tabs").tabs("refresh");
        });
        $("#statusWindows").resizable({
            animate: true,
            aspectRatio : false,
            ghost: true,
            handles: "e, s, se, sw",
            stop: (event, eventUI) ->
              SizeUtil.computeAreaSizes("statusWindows");
        });

        # height is set after width
        iCasaViewModel.mapHeight.subscribe(() ->
          if mapHeightSubscritionCalls > 0
            return; # if it was called, return.
          SizeUtil.initAreaSizes(iCasaViewModel.mapWidth(), iCasaViewModel.mapHeight());
          iCasaViewModel.updateMapSize();

          # manage map size changes
          $("#map").resize( (event) ->
            iCasaViewModel.updateMapSize();
          );

          # manage resize of the browser window
          $(window).resize( (event) ->
            SizeUtil.computeAreaSizes(null);
          );
          mapHeightSubscritionCalls = 1;
        );



        # start extensions mechanism using H-UBU
        hub.registerComponent(iCasaViewModel).createInstance(ConnectionWidget, {
          name : "GatewayConnectionWidget-1",
          buttonId : "connection-status-button"
        }).createInstance(GatewayConnectionMgrImpl, {
          name : "GatewayConnectionMgr-1",
        }).createInstance(ShellManager, {
          name: "ShellManager-1",
          outputId: "shellOutput",
          url: $("#map").attr("gatewayURL").replace(/\/$/, "");
        }).start();

        # widget loading
        extensionsElt = $("#icasa-extensions");
        widgetsElt = extensionsElt.attr("icasa-widgets");
        if (widgetsElt)
          widgets = widgetsElt.split(",");
          for widgetStr in widgets
            do (widgetStr) ->
              widgetName = widgetStr.replace /^\s+|\s+$/g, "";
              if (widgetName != "")
                require(["/simulator/widgets/" + widgetName + "/" + widgetName + ".js"], () ->
                  console.log("widget " + widgetName + " loaded !!!");
                );

        # BUG #95, unable to fix it, coffeescript compilation problem !!!
        # plugin loading
        pluginsElt = extensionsElt.attr("icasa-plugins");
        if (pluginsElt)
          plugins = pluginsElt.split(",");
          for pluginStr in plugins
            do (pluginStr) ->
              pluginName = pluginStr.replace /^\s+|\s+$/g, "";
              if (pluginName != "")
                require(["/simulator/plugins/" + pluginName + "/" + pluginName + ".js"], () ->
                  console.log("plugin " + pluginName + " loaded !!!");
                );
);

