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
        # Get top products
        DataModel.collections.topProducts = new DataModel.Collections.Products()
        DataModel.collections.topProducts.getTopProducts(10)
        # Get first page product list
        DataModel.collections.availableProducts = new DataModel.Collections.Products()
         #Get owned devices
        DataModel.collections.ownedDevices = new DataModel.Collections.OwnedDevices()
        DataModel.collections.ownedDevices.fetch();
         #Get purshased devices
        DataModel.collections.purshasedDevices = new DataModel.Collections.PurshasedDevices()
        DataModel.collections.purshasedDevices.fetch();        
        # Get purshased products
        DataModel.collections.ownedProducts = new DataModel.Collections.OwnedProducts()
        #Get products only when clicking in tab
        $("#ownedProductLink").click(DataModel.collections.ownedProducts.updateOwnedModel)

        #get available categories
        DataModel.collections.categories = new DataModel.Collections.Categories()
        DataModel.collections.categories.fetch();

        #productsByCategories
        DataModel.collections.productsByCategories = new DataModel.Collections.FilteredProducts()

        mainView = new view.MainUserView();#ownedProducts, ownedDevicesCollection, carouselCollection, pageCollection, categories, productsByCategories
        ko.applyBindings(mainView)

);