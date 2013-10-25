
define(['jquery', 'backbone', 'underscore', 'domReady'],
     ($, BackBone, _) ->


         DataModel =
            Models : {}
            Collections : {}
            collections : {}
            models : {}
            resetState : ()->
                $.ajax(
                    type: 'DELETE',
                    url: gatewayURL + "/icasa/simulation",
                    success: () =>
                        DataModel.collections.zones.reset();
                        DataModel.collections.persons.reset();
                        DataModel.collections.devices.fetch(); #Get unremoved devices (real devices)
                    error :() =>
                        console.log "Unable to reset simulation in backend";
                );

         class DataModel.Models.Map extends BackBone.Model
            urlRoot : "/icasa/frontend/maps"

         class DataModel.Collections.Maps extends Backbone.Collection
            model : DataModel.Models.Map
            url :  "/icasa/frontend/maps"


         DataModel.collections.maps = new DataModel.Collections.Maps()
         DataModel.collections.maps.fetch();

         return DataModel;
);