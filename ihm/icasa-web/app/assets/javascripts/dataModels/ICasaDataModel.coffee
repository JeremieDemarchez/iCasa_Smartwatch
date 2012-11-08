
define(['backbone'],
     (Backbone) ->
         DataModel =
            Models : {}
            Collections : {}
            collections : {}

         serverUrl = "http://localhost:8080/icasa";

         class DataModel.Models.Device extends Backbone.Model
            urlRoot : "#server#/device".replace /#server#/, serverUrl

         class DataModel.Collections.Devices extends Backbone.Collection
            url: "#server#/devices".replace /#server#/, serverUrl
            model: DataModel.Models.Device

         DataModel.collections.devices = new DataModel.Collections.Devices();
         DataModel.collections.devices.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         return DataModel;
);
