define ['jquery','jquery.ui','bootstrap','underscore','knockback','knockout','components/model/productModel','backbone'], ($,ui,bs,_,kb,ko, DataModel,bb) ->

	# The accordion first configuration used in buyed products
	$(()->
  		$(".accordion-products .hidden-table:not(maccordion)").hide()
  		# $(".accordion-products table:first-child").show()
  		$('.carousel').carousel({interval: 5000})

  	)

	# Add a new Device
	window.addDevice = ()->
            deviceName = document.getElementById("inputDeviceName").value
            deviceUrl = document.getElementById("inputDeviceURL").value
            device = new DataModel.Model.OwnedDevice({name: deviceName, url: deviceUrl})
            device.save()
            alert("New Device Added")
            return true


	#Register the knockout custom handler to handle clicks in accordion table
	ko.bindingHandlers.bsAccordionTable = {
		init: (element) ->
			$(element).click(()->
				$(element).nextAll(".hidden-table").fadeToggle("fast")
			)
			return { controlsDescendantBindings: false}
	}

	#Register the knockout custom handler to the carousel bootstrap effect
	ko.bindingHandlers.bsPopover = {
		init: (element, valueAccessor) ->
			@val = ko.utils.unwrapObservable(valueAccessor())
			@options = { title: val.title, animation: val.animation, placement: val.placement, trigger: val.trigger, delay: val.delay,content: if $(val.target).html()? then $(val.target).html() else val.content }
			$(element).popover(@options)
			return { controlsDescendantBindings: false}
	}

	#Register the knockout custom handler to the jQuery.ui toggle effect
	ko.bindingHandlers.bsToggle = {
		init: (element, valueAccessor) ->
			val = ko.utils.unwrapObservable(valueAccessor())
			listElement = element.children

			firstElement = listElement[0]
			secondElement = listElement[1]
			# firstElement = "#"+val.firstElement
			# secondElement = "#"+val.secondElement
			effect = if val.effect? then val.effect else "blind"
			handler = new ToggleHandler(element, firstElement, secondElement, effect)
			return { controlsDescendantBindings: false}
	}

	#Register the knockout custom handler to register event when buy products
	ko.bindingHandlers.buyModalButton = {
		init: (element, valueAccessor, allBindingsAccessor, viewModel) ->
			handler = new BuyHandler(element, viewModel.model)
			return { controlsDescendantBindings: false}
	}

	class BuyHandler
		constructor:(@button, @model)->
			$(@button).click(@.buyProduct)
		buyProduct:()=>
			@.model.buyProduct()

	#Class to handle the toggle effect for each product.
	class ToggleHandler
		constructor:(element, @firstElement, @secondElement, @effect)->
			$(@secondElement).hide();
			$(@firstElement).show();
			$(element).click(@.handleEventIn, @.handleEventOut)
		handleEventIn:()=>
			$(@firstElement).toggle(@effect)
			$(@secondElement).toggle(@effect)
		handleEventOut:()=>
			$(@secondElement).toggle(@effect)
			$(@firstElement).toggle(@effect)

	class IdentifiedViewModel extends kb.ViewModel
		constructor:(@model)->
			@id = model.get('id')
			@name = kb.observable(model, 'name')
			@description = kb.observable(model, 'description')

	class VersionedViewModel extends IdentifiedViewModel
		constructor:(@model)->
			super(model)
			@version = kb.observable(model,'version')


	class ServiceViewModel extends VersionedViewModel
		constructor: (@model) ->
			super(@model)

	class CategoryViewModel extends IdentifiedViewModel
		constructor: (@model) ->
			super(model)

	class ApplicationViewModel extends VersionedViewModel
		constructor: (@model) ->
			super(model)

	class DeviceViewModel extends IdentifiedViewModel
	    constructor:(@model)->
	        super(model);
	        @imageURL = model.get('imageURL') ;

	class ProductViewModelBase extends IdentifiedViewModel
		constructor:(@model) ->
			super(model)
			@imageURL = model.get('imageURL')


			@lastVersion = model.get('lastVersion')
			#The collection of  services
			@services = kb.collectionObservable(new bb.Collection(model.get('services')), {view_model: ServiceViewModel})
			#The collection of applications
			@applications = kb.collectionObservable(new bb.Collection(model.get('applications')), {view_model: ApplicationViewModel})
			#The collection of devices
			@devices = kb.collectionObservable(new bb.Collection(model.get('devices')), {view_model: DeviceViewModel})
			#The collection of categories of the product
			@categories = model.get('categories')

			#These fields are used only to the template.
			@shortDescription = ko.computed(()=>
			 	if @description().length > 100
			 		shortD = @description().substring(0,100) + "..."
			 	else
			 		shortD = @description()
			 	return shortD;
			 )

	class ProductViewModelOwned extends ProductViewModelBase
		constructor: (@model) ->
			super(model)
			@modalId = "modal-application-owned-" + @id
			@modalIdRef = "#" + @modalId

	class ProductViewModel extends ProductViewModelBase
		constructor: (@model) ->
			super(model)
			@classItem = "item"
		saveModel: () ->
			@pmodel.save()

	class ProductViewModelGrid extends ProductViewModel
		constructor: (@model) ->
			super(model)
			@modalId = "modal-product-grid" + @id
			@modalIdRef = "#" + @modalId

			@modalTabDescriptionId = @modalId + "_desc"
			@modalTabDescriptionIdRef = "#" + @modalTabDescriptionId

			@modalTabServicesId = @modalId + "_serv"
			@modalTabServicesIdRef = "#" + @modalTabServicesId

			@modalTabAppsId = @modalId + "_apps"
			@modalTabAppsIdRef = "#" + @modalTabAppsId

	class OwnedDeviceViewModel #extends kb.ViewModel
		constructor:(@model) ->
			@id = model.get('id')
			@name = ko.observable(model.get('name'))
			@url = ko.observable(model.get('url'))
			@installedPackages = kb.collectionObservable(model.get('installedPackages'), {view_model: ApplicationViewModel})



	class MainUserView
		constructor:()->
			#@ownedProductsModel, @ownedDevicesModel, @availableProductsModel, @availableProductsModelGrid, @categoriesModel, @productsByCategory
			@ownedProducts = kb.collectionObservable(DataModel.collections.ownedProducts, {view_model: ProductViewModelBase})
			@ownedDevices = kb.collectionObservable(DataModel.collections.ownedDevices, {view_model: OwnedDeviceViewModel})
			#devices purshased 
			@purshasedDevices = kb.collectionObservable(DataModel.collections.purshasedDevices, {view_model: DeviceViewModel})
			# Configure the top 10 item class to display in carrousel
			@availableProducts = kb.collectionObservable(DataModel.collections.topProducts, {view_model: ProductViewModelGrid})
			@availableProducts.subscribe(@.addItemClass)
			# The available products
			@pageSize= 8
			@pageIndex= 0
			@availableProductsGrid = kb.collectionObservable(DataModel.collections.availableProducts, {view_model: ProductViewModelGrid})
			#Get the first page
			DataModel.collections.availableProducts.getNextPage(@.pageIndex, @.pageSize)
			#Souscribe to get newer pages on scroll down
			@categories = kb.collectionObservable(DataModel.collections.categories, {view_model: CategoryViewModel})
			$(window).scroll(@.fetchNewPage)

		getAllProducts:()=>
			@.availableProductsGrid.collection(DataModel.collections.availableProducts)

		addItemClass:(pmodels)->
			if pmodels.length > 0
				firstModel = pmodels[0]
				firstModel.classItem = 'active item'
		fetchNewPage:()=>
			if $(window).scrollTop() == $(document).height() - $(window).height()
				@.pageIndex++
				DataModel.collections.availableProducts.getNextPage(@.pageIndex, @.pageSize)
		installApplication:(application, device)=>
			device.model.installApplication(application.model)

		updateCategory :(category)=>
			if (category.id?)
				DataModel.collections.productsByCategories.getProducsByCategory(category.id)
				@.availableProductsGrid.collection(DataModel.collections.productsByCategories)
			else
				@.getAllProducts()





	class MainAdminView
		constructor:()->
			@pageSize= 8
			@pageIndex= 0

			# The available products, categories, services and applications
			@products = kb.collectionObservable(DataModel.collections.products, {view_model: ProductViewModelGrid});
			@categories = kb.collectionObservable(DataModel.collections.categories, {view_model: CategoryViewModel});
			@applications = kb.collectionObservable(DataModel.collections.applications, {view_model: ApplicationViewModel});
			@services = kb.collectionObservable(DataModel.collections.services, {view_model: ServiceViewModel});
			@devices = kb.collectionObservable(DataModel.collections.devices, {view_model: DeviceViewModel});
			#For selection, the selected Applications, Services, categories
			@chosenApplications = ko.observableArray([]);
			@chosenServices = ko.observableArray([]);
			@chosenCategories = ko.observableArray([]);
			@chosenDevices = ko.observableArray([]);

			#Get the first page of products
			DataModel.collections.products.getNextPage(@.pageIndex, @.pageSize)
			#Souscribe to get newer pages on scroll down
			$(window).scroll(@.fetchNewPage)
			#For the creation of new product
			@newProductName = ko.observable("")
			@newProductDescription = ko.observable("")
			@newProductVersion = ko.observable("1.0.0")
            #For the creation of new Application
			@newApplicationName = ko.observable("")
			@newApplicationDescription = ko.observable("")
			@newApplicationVersion = ko.observable("1.0.0")
			@newApplicationURL= ko.observable("")
            #For the creation of new Service
			@newServiceName = ko.observable("")
			@newServiceDescription = ko.observable("")
			@newServiceVersion = ko.observable("1.0.0");

            #For the creation of new Device

			@newDeviceName = ko.observable("")
			@newDeviceDescription = ko.observable("")


			@addProduct = ()=>
				version = new DataModel.Models.Version(version: @newProductVersion());
				versions = new DataModel.Collections.Versions([version]);
				#Get chosen identifiers
				chosenCategoriesId = _.map(@chosenCategories(), (_cat)=>
					return _cat.model;
				);
				chosenApplicationsId = _.map(@chosenApplications(), (_app)=>
					return _app.model;
				);
				chosenServicesId = _.map(@chosenServices(), (_service)=>
					return _service.model;
				);
				chosenDevicesId = _.map(@chosenDevices(), (_device)=>
                    return _device.model;
                );

				#Create the new Product model and save it into the backend
				product = new DataModel.Models.Product({name: @newProductName(), description: @newProductDescription(), versions: versions, categories: chosenCategoriesId, applications: chosenApplicationsId, services: chosenServicesId, devices: chosenDevicesId});

				product.save({}, {
                    success: ()->
                        product.setImage()
                });
				#add it to the local collection
				DataModel.collections.products.push(product);
				#Remove options for new additions.
				@chosenCategories.removeAll();
				@chosenServices.removeAll();
				@chosenApplications.removeAll();
				@newProductName("");
				@newProductDescription("");
				@newProductVersion("1.0.0");
#				if $("#newProductPicture").val()?
#					file = $("#newProductPicture").val();
#					product.setImage(file)

			@addApplication = ()=>
				application = new DataModel.Models.Application({name: @newApplicationName(), description: @newApplicationDescription(), version: @newApplicationVersion(), url: @newApplicationURL()})
				application.save()
				DataModel.collections.products.push(application)
			@addService = ()=>
				version = new DataModel.Models.Version(version: @newServiceVersion());
				versions = new DataModel.Collections.Versions([version]);
				service = new DataModel.Models.Service({name: @newServiceName(), description: @newServiceDescription(), version: @newServiceVersion(), versions: versions})
				service.save()
				DataModel.collections.services.push(service)
			@addDevice = ()=>
            	device = new DataModel.Models.Device({name: @newDeviceName(), description: @newDeviceDescription()})
            	device.save({},{
            	    success:()->
            	        device.setImage();
            	});
            	DataModel.collections.devices.push(device)

		fetchNewPage:()=>
			if $(window).scrollTop() == $(document).height() - $(window).height()
				@.pageIndex++;
				DataModel.collections.products.getNextPage(@.pageIndex, @.pageSize);


	return {MainUserView, MainAdminView}