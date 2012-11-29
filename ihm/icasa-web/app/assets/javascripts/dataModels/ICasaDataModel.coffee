
define(['backbone'],
     (Backbone) ->
         DataModel =
            Models : {}
            Collections : {}
            collections : {}

         serverUrl = "http://" + window.location.hostname + ":8080/icasa";

         class DataModel.Models.Device extends Backbone.Model
            urlRoot : "#server#/devices/device".replace /#server#/, serverUrl

         class DataModel.Collections.Devices extends Backbone.Collection
            url: "#server#/devices/devices".replace /#server#/, serverUrl
            model: DataModel.Models.Device

         class DataModel.Models.DeviceType extends Backbone.Model
            urlRoot : "#server#/devices/deviceType".replace /#server#/, serverUrl

         class DataModel.Collections.DeviceTypes extends Backbone.Collection
            url: "#server#/devices/deviceTypes".replace /#server#/, serverUrl
            model: DataModel.Models.DeviceType

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

         class DataModel.Models.Script extends Backbone.Model
           urlRoot : "#server#/scriptPlayer/script".replace /#server#/, serverUrl

         class DataModel.Collections.Scripts extends Backbone.Collection
           url: "#server#/scriptPlayer/scripts".replace /#server#/, serverUrl
           model: DataModel.Models.Script

         # initial import of data model
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

         DataModel.collections.devices = new DataModel.Collections.Devices();
         DataModel.collections.devices.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         DataModel.collections.deviceTypes = new DataModel.Collections.DeviceTypes();
         DataModel.collections.deviceTypes.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         return DataModel;
);
