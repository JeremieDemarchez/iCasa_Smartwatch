# Require.js allows us to configure shortcut alias
require.config({

    #By default load any module IDs from assets/javascripts
    baseUrl: '../../assets/javascripts',

    # paths MUST not include .js extension
    paths: {
        'bootstrap.dir' : 'frameworks/bootstrap',
        'bootstrap' : 'frameworks/bootstrap/js/bootstrap.min',
        'comp' : 'components',
        'domReady' : 'frameworks/require/require-domReady/domReady.min', # AMD module
        'handlebars' : 'frameworks/handlebars/handlebars-1.0.rc.1',
        'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min', # AMD module
        'jquery.ui.dir':'frameworks/jquery/ui/1.9/',
        'jquery.ui':'frameworks/jquery/ui/1.9/js/jquery-ui-1.9.0.custom',
        # TODO remove ui.touch when move to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'knockout' : 'frameworks/knockout/knockout-2.1.0', # AMD module
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
        'bootstrap': [
            'jquery'#,
             #'css!bootstrap.dir/css/bootstrap.min' #loaded by main html page
            #
            #'css!bootstrap.dir/css/bootstrap-responsive.min'
            ],

        'handlebars': {
            exports: "Handlebars"
        },

        'jquery.ui': {
            deps: ['jquery'],
            #     'css!jquery.ui.dir/css/smoothness/jquery-ui-1.9.0.custom.modified'],

            exports: 'window.jQuery.ui'
        },

        'jquery.ui.touch': ['jquery.ui'],

        'underscore': {
            exports: "_"
        }
    }

});

