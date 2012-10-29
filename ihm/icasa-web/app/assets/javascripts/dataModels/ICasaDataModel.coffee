
define(['backbone'],
     (Backbone) ->
         DataModel =
            Models : {}
            Collections : {}
            collections : {}

         class DataModel.Models.Device extends Backbone.Model
            urlRoot : "/service/device"

         class DataModel.Collections.Devices extends Backbone.Collection
            url: "/service/devices"
            model: DataModel.Models.Device

         DataModel.collections.devices = new DataModel.Collections.Devices();
         DataModel.collections.devices.fetch({
            success : (data) -> console.log(data);
            error : (err) -> throw err;
         });

         return DataModel;
);
