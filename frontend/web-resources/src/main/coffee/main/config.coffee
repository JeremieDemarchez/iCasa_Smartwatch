
# Require.js allows us to configure shortcut alias
require.config({

    waitSeconds: 15,

    # By default load any module IDs from assets/javascripts
    baseUrl: '/assets/javascripts',

    # paths MUST not include .js extension
    paths: {
        'atmosphere' : 'frameworks/atmosphere/jquery.atmosphere',
        'backbone' : 'frameworks/backbone/backbone-1.1.0.min',
        'backbone.debug' : 'frameworks/backbone/backbone.debug',
        'bootstrap.dir' : 'frameworks/bootstrap',
        'bootstrap' : 'frameworks/bootstrap/js/bootstrap.min',
        'comp' : 'components',
        'contracts' : 'contracts',
        'domReady' : 'frameworks/require/require-domReady/domReady.min', # AMD module
        'hammer' : 'frameworks/hammer/hammer', # AMD module
        'hammer-jquery' : 'frameworks/hammer/jquery.hammer', # AMD module
        'handlebars' : 'frameworks/handlebars/handlebars-1.0.rc.1',
        'hubu' : 'frameworks/hubu/h-ubu-1.0.0-min',
        #'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min', # AMD module
        'jquery.ui':'frameworks/jquery/ui/1.9/js/jquery-ui-1.9.0.custom.min',
        # TODO remove ui.touch when moving to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.resize' : 'frameworks/jquery/ba-resize/jquery.ba-resize.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'log4javascript' : 'frameworks/log4javascript/log4javascript',
        'knockback' : 'frameworks/knockback/knockback-0.17.2.min', # AMD module
        'knockout.debug' : 'frameworks/knockout/knockout-2.1.0.debug', # AMD module
        'knockout' : 'frameworks/knockout/knockout-3.0.0', # AMD module
        'modernizr' : 'frameworks/modernizr/modernizr.custom.min',
        'sammy' : 'frameworks/sammy/sammy-latest.min', # AMD module
        'templates' : 'templates',
        'locale' : 'locales',
        'underscore' : 'frameworks/underscore/underscore-min',
        'DeviceWidgetContract' : 'contracts/DeviceWidgetContract',
        'index'  :  'main/index',
        'map'  :  'main/map'
    },

    # Require.js plugins to handle other types of dependencies
    map: {
        '*': {
            'css': 'frameworks/require/require-css/css',
            'text': 'frameworks/require/require-text/text',
            'i18n': 'frameworks/require/require-i18n/i18n'
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
        }
    }

});



# workaround to have jquery module without double loading, jquery MUST have been loaded before
if ( typeof define == "function" && define.amd && define.amd.jQuery)
  define("jquery", [], () ->
    return jQuery;
  );

