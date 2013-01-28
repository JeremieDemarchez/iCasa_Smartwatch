
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
        'domReady' : 'frameworks/require/require-domReady/domReady.min', # AMD module
        'handlebars' : 'frameworks/handlebars/handlebars-1.0.rc.1',
        'hubu' : 'frameworks/hubu/hubu-all',
        'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min', # AMD module
        'jquery.ui':'frameworks/jquery/ui/1.9/js/jquery-ui-1.9.0.custom',
        # TODO remove ui.touch when move to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.resize' : 'frameworks/jquery/ba-resize/jquery.ba-resize.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'knockback' : 'frameworks/knockback/knockback', # AMD module
        'knockout' : 'frameworks/knockout/knockout-2.1.0.debug', # AMD module
        'modernizr' : 'frameworks/modernizr/modernizr.custom.min',
        'sammy' : 'frameworks/sammy/sammy-latest.min', # AMD module
        'templates' : 'templates',
        'underscore' : 'frameworks/underscore/underscore-min'
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
            'jquery',
            # 'css!bootstrap.dir/css/bootstrap.min', //loaded by main html page
            'css!bootstrap.dir/css/bootstrap-responsive.min'],

        'handlebars': {
            exports: "Handlebars"
        },

        'hubu': {
            exports: "hub"
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
        }
    }

});

class SizeUtil
  @getViewportSize = () ->
    e = window;
    a = 'inner';
    if (!('innerWidth' in window))
      a = 'client';
      e = document.documentElement || document.body;
    return { width : e[ a+'Width' ], height : e[ a+'Height' ] };

  @initAreaSizes = (mapWidth, mapHeight) ->
    map = $("#map");
    map.width(mapWidth);
    map.height(mapHeight);
    @computeAreaSizes("map");

  @computeAreaSizes = (resizedAreaId) ->
    viewportSize = @.getViewportSize();

    # hypothesys : areaBorderSize = padding + marging + border = 10 + 3 + 15 = 28px
    areaBorderSize = 28;
    map = $("#map");
    mapWidth = map.width();
    mapHeight = map.height();
    actionTabs = $("#actionTabs");
    actionTabsWidth = actionTabs.width();
    actionTabsHeight = actionTabs.height();
    availableWidth = viewportSize.width - (4 * areaBorderSize) - 5;




    if ((resizedAreaId == undefined) || (resizedAreaId == null) || (resizedAreaId == "map"))
      actionTabs.width(availableWidth - mapWidth);
      actionTabs.height(mapHeight);
    else
      map.width(availableWidth - actionTabsWidth);
      map.height(map.width() / mapWidth * mapHeight);

    if(actionTabs.width() < 200)
      map.width(availableWidth);
      map.height(map.width() / mapWidth * mapHeight);
      actionTabs.width(map.width());

    if(actionTabs.width() > 900)
      map.width(availableWidth/3);
      map.height(map.width() / mapWidth * mapHeight);
      actionTabs.width(availableWidth - map.width());


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
    'viewModels/iCasaViewModel',
    'dataModels/ICasaDataModelNotifs',
    'bootstrap',
    'domReady',
    'jquery.resize',
    'backbone.debug'
    ],
    ($, ui, ko, ICasaViewModel, iCasaNotifSocket) ->

        iCasaViewModel = new ICasaViewModel( {
          id: "PaulHouse",
          imgSrc: "assets/images/maps/paulHouse.png"
        });
        ko.applyBindings(iCasaViewModel);

        $(".slider" ).slider();
        $("#tabs").tabs({
            heightStyle: "fill"
        });

        $("#map").resizable({
            animate: true,
            aspectRatio : true,
            ghost: true
            stop: (event, eventUI) ->
              SizeUtil.computeAreaSizes("map");
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
            handles: "e, s, se, sw"
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
);