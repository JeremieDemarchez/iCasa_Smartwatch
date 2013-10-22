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
    'components/VersionManagerWidget',
    'components/ICasaShellManagerImpl',
    'domReady',
    'jquery.resize'
    ],
    ($, ui, ko, hub, ICasaViewModel, iCasaNotifSocket, ConnectionWidget, GatewayConnectionMgrImpl, VersionManagerWidget, ShellManager) ->

        mapName = $("#map").attr("mapId");
        mapImgUrl = $("#map").attr("mapImgSrc");
        mapHeightSubscritionCalls = 0;
        iCasaViewModel = new ICasaViewModel( {
          id: mapName,
          imgSrc: mapImgUrl
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
        }).createInstance(VersionManagerWidget, {
          name: "VersionManagerWidget-1",
          elementId: "compatibilityWarn"
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

