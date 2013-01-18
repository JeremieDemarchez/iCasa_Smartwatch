
define(['backbone', 'underscore'],
     (Backbone, _) ->

         cx_backbone_common =
           sync: (method, model, options) ->
             # Changed attributes will be available here if model.saveChanges was called instead of model.save
             if method == 'update' && model.changedAttributes()
               options.data = JSON.stringify(model.changedAttributes())
               options.contentType = 'application/json';
             Backbone.sync.call(this, method, model, options)

         cx_backbone_model =
         # Calling this method instead of set will force sync to only send changed attributes
         # Changed event will not be triggered until after the model is synced
           saveChanges: (attrs) ->
             @save(attrs, {wait: true})

         _.extend(Backbone.Model.prototype, cx_backbone_common, cx_backbone_model)
         _.extend(Backbone.Collection.prototype, cx_backbone_common)

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

         DataModel.collections.deviceTypes = new DataModel.Collections.DeviceTypes();
         DataModel.collections.deviceTypes.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

#         DataModel.collections.devices.subscribe();

         return DataModel;
);
