require([
    'jquery',
    'jquery.ui',
    'bootstrap',
    'knockback',
    'backbone',
    'knockout',
    'components/model/productModel',
    'components/modelView/productView',
    ],
	($, ui,bs,kb, bb,ko, DataModel, view) ->
        # Get first page product list
        DataModel.collections.products = new DataModel.Collections.Products()
                #get available categories
        DataModel.collections.categories = new DataModel.Collections.Categories()
        DataModel.collections.categories.fetch();
        #Get available applications
        DataModel.collections.applications = new DataModel.Collections.Applications()
        DataModel.collections.applications.fetch();
        #get available services
        DataModel.collections.services = new DataModel.Collections.Services()
        DataModel.collections.services.fetch();

        DataModel.collections.devices = new DataModel.Collections.Devices()
        DataModel.collections.devices.fetch();

        mainView = new view.MainAdminView()
        ko.applyBindings(mainView)

);