
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
        'hubu' : 'frameworks/hubu/h-ubu-1.0.0-min',
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
    # hypothesys : areaBorderSize = padding + marging + border = 10 + 3 + 15 = 28px
    areaBorderSize = 28;
    actionTabMinWidth = parseInt($('#actionTabs').css('min-width'), 10) ;
    availableWidth = @.getViewportSize().width - (4 * areaBorderSize) - 25;
    #calculate width and height to fith in available space
    calculatedWidth = availableWidth - actionTabMinWidth;
    calculatedHeight = (mapHeight / mapWidth) * calculatedWidth;

    if availableWidth >= 900 && calculatedWidth <= mapWidth
        map.width(calculatedWidth);
        map.height(calculatedHeight);
    else
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
    availableWidth = viewportSize.width - (4 * areaBorderSize) - 25;
    if ((resizedAreaId == undefined) || (resizedAreaId == null) || (resizedAreaId == "map"))
      actionTabs.width(availableWidth - mapWidth);
      actionTabs.height(mapHeight);
    else
      map.width(availableWidth - actionTabsWidth);
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
    'jquery.resize'
    ],
    ($, ui, ko, ICasaViewModel, iCasaNotifSocket) ->

        #DO NOT MOVE following instruction, container must be defined resizable before nested resizable elements
        $("#map").resizable({
          animate: true,
          aspectRatio : true,
          ghost: true,
          stop: (event, eventUI) ->
            SizeUtil.computeAreaSizes("map");
        });

        mapName = $("#map").attr("gatewayURL");
        mapImgUrl = $("#map").attr("mapImgSrc");

        iCasaViewModel = new ICasaViewModel( {
          id: mapName,
          imgSrc: mapImgUrl
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

        require(["/plugins/fake/fake.js"], () ->
          console.log("module 1 loaded !!!");
        );
        require(["/plugins/fake1/fake1.js"], () ->
          console.log("module 2 loaded !!!");
        );
);

