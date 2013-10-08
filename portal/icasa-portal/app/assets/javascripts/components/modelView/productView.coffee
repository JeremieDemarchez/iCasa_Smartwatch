define ['jquery','jquery.ui','bootstrap','underscore','knockout'], ($, ui, bs, _, ko) ->

  # The accordion first configuration used in buyed products
  $(()->
    $(".accordion-products .hidden-table:not(maccordion)").hide()
    # $(".accordion-products table:first-child").show()
  )

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
    # console.log element
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

  class ProductViewModel
    constructor:(model) ->
      @id = model.id;
      @name = model.name;
      @imageURL = model.imageURL;
      @description = model.description;
      @classItem = "item";
      @detailsRef = model.detailsURL;

      #These fields are used only to the template.
      @shortDescription = ko.computed(() =>
        if @description.length > 100
          shortD = @description.substring(0,100) + "..."
        else
          shortD = @description
        return shortD;
      )

  class ProductViewModelCollection
    constructor: () ->
      @productsInCarousel = ko.observableArray([
        new ProductViewModel {
          id: "devAndTest",
          name: "Develop And Test",
          imageURL: "assets/images/lifecycle/develop-banner.png",
          description: "TODO",
          classItem: 'active item',  # add it to select activated item at startup
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "runAndAdministrate",
          name: "Run And Administrate",
          imageURL: "assets/images/lifecycle/run-banner.png",
          description: "TODO",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "distribute",
          name: "Distribute" ,
          imageURL: "assets/images/lifecycle/distribute-banner.png",
          description: "TODO",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "configureAndUse",
          name: "Configure And Use",
          imageURL: "assets/images/lifecycle/configure-banner.png",
          description: "TODO",
          detailsRef: "#TODO"}
      ]);
      @productsNotInCarousel = ko.observableArray([
        new ProductViewModel {
          id: "develop",
          name: "Develop",
          imageURL: "assets/images/lifecycle/develop.png",
          description: "Design your digital apps using ipojo-ide and cilia-ide.",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "test",
          name: "Test",
          imageURL: "assets/images/lifecycle/test.png",
          description: "Test your digital apps using iCasa simulator.",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "runtime",
          name: "Prepare your runtime" ,
          imageURL: "assets/images/lifecycle/runtime.png",
          description: "Prepare your runtime using xxx.",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "upload",
          name: "Upload" ,
          imageURL: "assets/images/lifecycle/upload.png",
          description: "Upload and share your app using xxx.",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "download",
          name: "Download",
          imageURL: "assets/images/lifecycle/download.png",
          description: "Download and install your app using xxx.",
          detailsRef: "#TODO"},
        new ProductViewModel {
          id: "configure",
          name: "Configure your app" ,
          imageURL: "assets/images/lifecycle/configure.png",
          description: "Configure your app using  iCasa dashboard.",
          detailsRef: "#TODO"},
      ]);

  return {ProductViewModelCollection, ProductViewModel }