// Filename: main.js

// Require.js allows us to configure shortcut alias
require.config({

    //By default load any module IDs from assets/javascripts
    baseUrl: '../../assets/javascripts',

    paths: {
        'backbone' : 'frameworks/backbone/backbone-min',
        'bootstrap.dir' : 'frameworks/bootstrap',
        'comp' : 'components',
        'hubu' : 'frameworks/hubu/hubu-all',
        'jquery' : 'frameworks/jquery/core/jquery-1.8.2.min',
        'jquery.ui' : 'frameworks/jquery/ui/js/jquery-ui-1.8.24.custom.min',
        //TODO remove ui.touch when move to jquery.ui 1.9 (will manage touch events)
        'jquery.ui.touch' : 'frameworks/jquery/ui.touch/jquery-ui-touch-punch.min',
        'jquery.mobile' : 'frameworks/jquery/mobile/1.2.0/jquery.mobile-1.2.0.min',
        'modernizr' : 'frameworks/modernizr/modernizr.custom.min',
        'underscore' : 'frameworks/underscore/underscore-min'
    },

    map: {
        '*': {
            'css': 'frameworks/require/require-css/css'
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
            'css!bootstrap.dir/css/bootstrap-responsive.min',
            'bootstrap.dir/js/bootstrap.min'],

        'hubu': {
            exports: "hub"
        },

        // does export nothing
        'jquery.ui.touch': ['jquery.ui'],

        'underscore': {
            exports: "_"
        }
    }

});

// launch application
require([
    'jquery',
    'hubu',
    'comp/backend',
    'comp/frontend'
    ], function($, hub, backend, frontend) {

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
