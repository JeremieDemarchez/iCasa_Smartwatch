
# Require.js allows us to configure shortcut alias
require.config({

    waitSeconds: 15,

    # By default load any module IDs from assets/javascripts
    baseUrl: '../../assets/javascripts',

    # paths MUST not include .js extension
    paths: {
        'atmosphere' : 'frameworks/atmosphere/jquery.atmosphere',
        'backbone' : 'frameworks/backbone/backbone-min',
        'backbone.debug' : 'frameworks/backbone/backbone.debug',
        'bootstrap.dir' : 'frameworks/bootstrap',
        'bootstrap' : 'frameworks/bootstrap/js/bootstrap.min',
        'comp' : 'components',
        'contracts' : 'contracts',
        'domReady' : 'frameworks/require/require-domReady/domReady.min', # AMD module
        'hammer' : 'frameworks/hammer/hammer', # AMD module
        'hammer-jquery' : 'frameworks/hammer/jquery.hammer', # AMD module
        'handlebars' : 'frameworks/handlebars/handlebars-1.0.rc.1',
        'hubu' : 'frameworks/hubu/h-ubu-1.0.0',
        # 'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min', # AMD module
        'jquery.ui':'frameworks/jquery/ui/1.9/js/jquery-ui-1.9.0.custom',
        # TODO remove ui.touch when moving to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.resize' : 'frameworks/jquery/ba-resize/jquery.ba-resize.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'log4javascript' : 'frameworks/log4javascript/log4javascript',
        'knockback' : 'frameworks/knockback/knockback', # AMD module
        'knockout' : 'frameworks/knockout/knockout-2.1.0.debug', # AMD module
        'modernizr' : 'frameworks/modernizr/modernizr.custom.min',
        'sammy' : 'frameworks/sammy/sammy-latest.min', # AMD module
        'templates' : 'templates',
        'underscore' : 'frameworks/underscore/underscore-min',
        'DeviceWidgetContract' : 'contracts/DeviceWidgetContract'
    },

    # Require.js plugins to handle other types of dependencies
    map: {
        '*': {
            'css': 'frameworks/require/require-css/css',
            'text': 'frameworks/require/require-text/text'
        }
    },

    # configuration of libraries not packaged using Require.js
    shim: {
        'atmosphere': {
          deps: ['jquery']

          exports: 'jQuery.atmosphere'
        },

        'backbone': {
            # These script dependencies should be loaded before loading
            # backbone.js
            deps: ['underscore', 'jquery'],
            # Once loaded, use the global 'Backbone' as the
            # module value.
            exports: 'Backbone'
        },

        'backbone.debug': {
            deps: ['backbone']
        },

        'bootstrap': [
            'jquery'
            # 'css!bootstrap.dir/css/bootstrap.min', //loaded by main html page
            # 'css!bootstrap.dir/css/bootstrap-responsive.min',
        ],

        'handlebars': {
            exports: "Handlebars"
        },

        'hubu': {
            exports: "hub"
        },

        'log4javascript' : {
            exports: "log4javascript"
        },

        'modernizr': {
            exports: "window.Modernizr"
        },

        'jquery.ui': {
            deps: ['jquery'],

            exports: 'window.jQuery.ui'
        },

        'jquery.ui.touch': ['jquery.ui'],

        'jquery.resize': ['jquery.ui'],

        'underscore': {
            exports: "_"
        },

        'hammer' : {
            deps:['jquery'],
            exports:'Hammer'
        }
    }

});


# workaround to have jquery module without double loading, jquery MUST have been loaded before
if ( typeof define == "function" && define.amd && define.amd.jQuery)
  define("jquery", [], () ->
    return jQuery;
  );


mapRealSizeWidth = 10;
mapRealSizeHeight = 10;

class SizeUtil

  @computeMapImgSize = (imgSrc) ->
    imgSrcNoCache = imgSrc + '?cache=' + Date.now();
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
    $("#tabs").tabs("refresh");

    statusWindows = $("#statusWindows");
    statusWindowsWidth = statusWindows.width();
    statusWindows.width(viewportSize.width - (2 * areaBorderSize));
    statusWindowsHeight = statusWindows.height();

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
    'domReady',
    'jquery.resize'
    ],
    ($, ui, ko, hub, ICasaViewModel, iCasaNotifSocket, ConnectionWidget, GatewayConnectionMgrImpl) ->

        mapName = $("#map").attr("mapId");
        mapImgUrl = $("#map").attr("mapImgSrc");

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
        );

        # start extensions mechanism using H-UBU
        hub.registerComponent(iCasaViewModel).createInstance(ConnectionWidget, {
          name : "GatewayConnectionWidget-1",
          buttonId : "connection-status-button"
        }).createInstance(GatewayConnectionMgrImpl, {
          name : "GatewayConnectionMgr-1",
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
                require(["/widgets/" + widgetName + "/" + widgetName + ".js"], () ->
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
                require(["/plugins/" + pluginName + "/" + pluginName + ".js"], () ->
                  console.log("plugin " + pluginName + " loaded !!!");
                );
);

