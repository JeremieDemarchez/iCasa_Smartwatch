define(['backbone', 'underscore'],
	(bb, _) ->

#define (['backbone'], (bb) ->
		DataModel =
		 	Models : {}
		 	Collections: {}
		 	collections: {}
		 	models : {}

		class DataModel.Models.Service extends bb.Model
			urlRoot: 'catalog/services'
			defaults:
				name: 'msn'
				description: 'msd'
				version: '0.0.0'

		class DataModel.Collections.Services extends bb.Collection
	 		url: 'catalog/services'
			model: DataModel.Models.Service

		class DataModel.Models.Application extends bb.Model
	 		urlRoot: 'catalog/applications'
			defaults:
				name: 'man'
				description: 'mad'
				version: '0.0.0'
		# ApplicationModel.setup()

		class DataModel.Collections.Applications extends bb.Collection
			url: 'catalog/applications'
			model: DataModel.Models.Application

		class DataModel.Models.Device extends bb.Model
			urlRoot: 'catalog/devices'
			defaults:
				name: 'dn'
				description: 'dd'
			setImage:(file)=>
				dataToSend = new FormData(document.forms.namedItem("deviceForm"));

				oReq = new XMLHttpRequest();
				oReq.open("POST", 'catalog/devices/'+@.id+'/image', true);
				imodel = @;
				oReq.onload = (oEvent) =>
					if oReq.status == 200
						@.set('imageURL', "assets/images/devices/"+ @.id+".jpg");
					else
						alert("Error when uploading");

				oReq.send(dataToSend);
				console.log "sending data"				

		class DataModel.Collections.Devices extends bb.Collection
			url: 'catalog/devices'
			model: DataModel.Models.Device

			

		class DataModel.Models.Category extends bb.Model
			defaults:
				name: 'generic'
				description: 'Generic product'

		class DataModel.Collections.Categories extends bb.Collection
			model: DataModel.Models.Category
			url: "catalog/categories"

		class DataModel.Models.Version extends bb.Model
			defaults:
				version: '1.0.0'

		class DataModel.Collections.Versions extends bb.Collection
			model: DataModel.Models.Version

		class DataModel.Models.Product extends bb.Model

			constructor: ()->
				super;
			urlRoot: 'catalog/products'

			defaults:
				name: 'my product name'
				description: 'my description'
				imageURL: 'assets/images/products/default.jpg'

			buyProduct:()->
				#only send product ID as part of URL, user association handled by secure social
				$.ajax(
					type: 'PUT',
					url: 'user/products/' + @.id,
				);
			setImage:(file)=>
				dataToSend = new FormData(document.forms.namedItem("productForm"));
				oReq = new XMLHttpRequest();
				oReq.open("POST", 'catalog/products/'+@.id+'/image', true);
				imodel = @;
				oReq.onload = (oEvent) =>
					if oReq.status == 200
						@.set('imageURL', "assets/images/products/"+ @.id+".jpg");
					else
						alert("Error when uploading");
				oReq.send(dataToSend);



		class DataModel.Collections.OwnedProducts extends bb.Collection
			model: DataModel.Models.Product
			url: 'user/products'
			constructor:()->
				super;

			updateOwnedModel:()=>
				@.fetch();

		class DataModel.Models.OwnedDevice extends bb.Model
				constructor: ()->
					super;
					@.on("sync", @.getInstalledApplication());


				getInstalledApplication:()=>
					@appurl = @.get("url")+"/appstore/applications"
					$.ajax({
						url: @.appurl,
						success: @.getAppsSuccess,
						error: @.getAppsError
						})
					# $.get(@.appurl, (data)=>
					# 	console.log "loaded apps" + data
					# 	# console.log data
					# 	# @.set(data)
					# 	# console.log @
					# )
					return;
				getAppsSuccess:(data,  textStatus, jqXHR)=>
					json_data = JSON.stringify(data);
					@.set(data);
				getAppsError:(jqXHR, textStatus, errorThrown)=>
					console.log "error when getting installed applications: " + errorThrown;


				installApplication: (application)->
					$.ajax(
		   				type: "POST",
		   				url: @.appurl,
		   				data: {
	        				'location':application.get("url")
	    				},
					);
				urlRoot: 'user/ownedDevices'
				defaults:
					name: 'generic'
					# installedPackages: new ApplicationModelCollection()


		class DataModel.Collections.OwnedDevices extends bb.Collection
			model: DataModel.Models.OwnedDevice
			url: '/user/ownedDevices'
			constructor:()->
				super;
		
		class DataModel.Collections.PurshasedDevices extends bb.Collection
			model: DataModel.Models.Device
			url: '/user/devices'
			constructor:()->
				super;

		class DataModel.Collections.Products extends bb.Collection

			url: 'catalog/products'
			model: DataModel.Models.Product
			constructor:()->
				@totalPages = 0;
				super;
			getTopProducts: (topProducts) ->
				@.fetch({ data: $.param({ topNumber: topProducts})});

			getNextPage:(currentPage, productsPerPage) ->
				@.fetch({ data: $.param({ page: currentPage, productsPerPage: productsPerPage}), add: true});

			parse: (response) ->
				#test if the list of products is located in an internal list called products
				if response.totalPages
					@.totalPages = response.totalPages;
				if response.products
	   		 		return response.products;
	   		 	else
	   		 		return response;

		class DataModel.Collections.FilteredProducts extends DataModel.Collections.Products
			url: 'catalog/products';
			constructor:()->
				@urlBase = 'catalog/products';
				@totalPages = 0;
				super;

			getProducsByCategory:(categoryId) ->
				@.url = @urlBase + '/category/'+categoryId;
				@.fetch();
				@.url = @urlBase;

		class DataModel.Models.DPSoftware extends bb.Model
		 	constructor: () ->
		 		super
		 	defaults:
		 		name: 'Application name'
		 		description: 'Application description'


		class DataModel.Collections.DPSoftwares extends bb.Collection
			constructor:()->
				super;

		return DataModel;
);