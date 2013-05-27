
define(['jquery', 'backbone', 'underscore', 'hubu', 'contracts/DataModelConnectionMgr', 'domReady'],
     ($, Backbone, _, hub, DataModelConnectionMgr) ->

         gatewayURL = $("#map").attr("gatewayURL").replace(/\/$/, "");

         serverUrl = gatewayURL + "/icasa";

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
                        DataModel.collections.devices.reset(); #TODO potential bug if there is real devices
                    error :() =>
                        console.log "Unable to reset simulation in backend";
                );


         class DataModel.Models.Backend extends Backbone.Model
            urlRoot : "#server#/backend".replace /#server#/, serverUrl

         class DataModel.Models.Frontend extends Backbone.Model
            urlRoot : "/frontend"

         class DataModel.Models.Device extends Backbone.Model
            urlRoot : "#server#/devices/device".replace /#server#/, serverUrl

         class DataModel.Models.Device.Property extends Backbone.Model
          idAttribute: "name"
          defaults:
              visible: false

         class DataModel.Models.Device.Properties extends Backbone.Collection
              model: DataModel.Models.Device.Property

         class DataModel.Collections.Devices extends Backbone.Collection
            url: "#server#/devices/devices".replace /#server#/, serverUrl
            model: DataModel.Models.Device

         class DataModel.Models.DeviceType extends Backbone.Model
            urlRoot : "#server#/devices/deviceType".replace /#server#/, serverUrl

         class DataModel.Collections.DeviceTypes extends Backbone.Collection
            url: "#server#/devices/deviceTypes".replace /#server#/, serverUrl
            model: DataModel.Models.DeviceType

         class DataModel.Collections.SimulatedDeviceTypes extends Backbone.Collection
            url: "#server#/devices/simulatedDeviceTypes".replace /#server#/, serverUrl
            model: DataModel.Models.DeviceType

         class DataModel.Models.PersonType extends Backbone.Model
            urlRoot : "#server#/persons/personType".replace /#server#/, serverUrl

         class DataModel.Collections.PersonTypes extends Backbone.Collection
            url: "#server#/persons/personTypes".replace /#server#/, serverUrl
            model: DataModel.Models.PersonType

         class DataModel.Models.Person extends Backbone.Model
            urlRoot : "#server#/persons/person".replace /#server#/, serverUrl

         class DataModel.Collections.Persons extends Backbone.Collection
            url: "#server#/persons/persons".replace /#server#/, serverUrl
            model: DataModel.Models.Person

         class DataModel.Models.Zone extends Backbone.Model
           urlRoot : "#server#/zones/zone".replace /#server#/, serverUrl

         class DataModel.Collections.Zones extends Backbone.Collection
           url: "#server#/zones/zones".replace /#server#/, serverUrl
           model: DataModel.Models.Zone

         class DataModel.Models.Clock extends Backbone.Model
           urlRoot : "#server#/clocks/clock".replace /#server#/, serverUrl

         class DataModel.Models.Script extends Backbone.Model
           urlRoot : "#server#/scriptPlayer/script".replace /#server#/, serverUrl

         class DataModel.Collections.Scripts extends Backbone.Collection
           url: "#server#/scriptPlayer/scripts".replace /#server#/, serverUrl
           model: DataModel.Models.Script

         #initial backend and frontend information
         DataModel.models.backend = new DataModel.Models.Backend();
         DataModel.models.backend.fetch();
         DataModel.models.frontend = new DataModel.Models.Frontend();
         DataModel.models.frontend.fetch();

         # initial import of data model
         DataModel.models.clock = new DataModel.Models.Clock({id: "default"});
         DataModel.models.clock.fetch({
           success : (data) -> console.log(data);
           error : (err) -> throw err;
         });

         DataModel.collections.scripts = new DataModel.Collections.Scripts();
         DataModel.collections.scripts.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.zones = new DataModel.Collections.Zones();
         DataModel.collections.zones.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.persons = new DataModel.Collections.Persons();
         DataModel.collections.persons.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.personTypes = new DataModel.Collections.PersonTypes();
         DataModel.collections.personTypes.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.devices = new DataModel.Collections.Devices();
         DataModel.collections.devices.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.deviceTypes = new DataModel.Collections.SimulatedDeviceTypes();
         DataModel.collections.deviceTypes.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         # component that will manage remote Data model connections
         class DataModelMgrImpl extends DataModelConnectionMgr
           @hub : null;
           @isConnected : false;

           @getComponentName: () ->
             return 'DataModelMgrImpl';

           @configure: (theHub, configuration) =>
             @hub = theHub;
             @isConnected = false;

             @hub.provideService({
               component: @,
               contract: DataModelConnectionMgr
             });

           @start : () =>

           @stop : () =>

         hub.registerComponent(DataModelMgrImpl);

         return DataModel;
);
