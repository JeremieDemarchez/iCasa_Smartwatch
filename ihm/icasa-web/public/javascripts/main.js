// Filename: main.js

// Require.js allows us to configure shortcut alias
require.config({

    //By default load any module IDs from assets/javascripts
    baseUrl: '../../assets/javascripts',

    paths: {
        'backbone' : 'frameworks/backbone/backbone-min',
        'bootstrap.dir' : 'frameworks/bootstrap',
        'bootstrap' : 'frameworks/bootstrap/js/bootstrap.min',
        'comp' : 'components',
        'hubu' : 'frameworks/hubu/hubu-all',
        'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min',
        'jquery.ui.dir':'frameworks/jquery/ui/1.9/',
        'jquery.ui':'frameworks/jquery/ui/1.9/js/jquery-ui-1.9.0.custom',
        //TODO remove ui.touch when move to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'modernizr' : 'frameworks/modernizr/modernizr.custom.min',
        'underscore' : 'frameworks/underscore/underscore-min'
    },

    map: {
        '*': {
            'css': 'frameworks/require/require-css/css',
            'text': 'frameworks/require/require-text/text'
        }
    },

    shim: {
        'backbone': {
            //These script dependencies should be loaded before loading
            //backbone.js
            deps: ['underscore', 'jquery'],
            //Once loaded, use the global 'Backbone' as the
            //module value.
            exports: 'Backbone'
        },

        'bootstrap': [
            'jquery',
            'css!bootstrap.dir/css/bootstrap.min',
            'css!bootstrap.dir/css/bootstrap-responsive.min'],

        'hubu': {
            exports: "hub"
        },

        'jquery.ui.touch': ['jquery.ui'],

        'underscore': {
            exports: "_"
        }
    }

});

define('jquery.ui',
    ['jquery',
     'css!jquery.ui.dir/css/smoothness/jquery-ui-1.9.0.custom.min'],
    function () {
      return window.jQuery.ui;
});

// launch application
require([
    'jquery',
    'hubu',
    'comp/backend',
    'comp/frontend',
    'jquery.ui'
    ], function($, hub, backend, frontend, ui) {

        $(document).ready(function(){

            hub.registerComponent(backend)
                .registerComponent(frontend, {
                    loginId : '#login',
                    logoutId : '#logout',
                    statusId : '#status'
                })
                .start();
        });
});
