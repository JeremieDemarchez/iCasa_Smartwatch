# launch application
require([
    'jquery',
    'jquery.ui',
    'knockout',
    'knockback',
    'backbone',
    'dataModels/IndexDataModel',
    'i18n!locales/nls/locale',
    'domReady'
    ],
    ($, ui, ko, kb,bb, DataModel, locale) ->

        class MapViewModel extends kb.ViewModel
            constructor:(model)->
                @id = kb.observable(model,'id');
                @name = kb.observable(model,'name');
                @description = kb.observable(model,'description');
                @gatewayURL = kb.observable(model,'gatewayURL');
                @imgFile = kb.observable(model,'imgFile');
                @libs = kb.observable(model,'libs');


        class IndexViewModel
            constructor:()->
                @maps = kb.collectionObservable(DataModel.collections.maps, {view_model: MapViewModel});
                @getLocaleMessage = (name) ->
                    return locale[name];


        viewModel = new IndexViewModel();
        ko.applyBindings(viewModel);

);

