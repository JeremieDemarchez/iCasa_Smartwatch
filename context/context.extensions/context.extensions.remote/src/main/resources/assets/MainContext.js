var mixitup, mixCategories;

/**PAGE MANAGEMENT**/
$(document).ready(function() {
    launchPage('#ContextModel');

    $('ul#MainMenu li').click(function(e){
        e.preventDefault();

        switch(this.id){
            case 'ContextModelMenu':
                var page = '#ContextModel';
                break;
            case 'ContextProvidersMenu':
                var page = '#ContextProviders';
                break;
            case 'ContextFactoriesMenu':
                var page = '#ContextFactories';
                break;
            case 'ContextApplicationsMenu':
                var page = '#ContextApplications';
                break;
        }
        changeActiveMenu(this);
        mixItUp_changeDefaultFilter(page, "all");
        changePageDisplay(page);
    });
});

function changeActiveMenu(menu){
        $(".active").removeClass('active');
        $(menu).addClass('active');
    }

function changePageDisplay(page){
    var pages = ['#ContextModel', '#ContextProviders', '#ContextFactories', '#ContextApplications'];

    $.each(pages,function(index, p){
        clearPage(p);
        if(p == page) {
            $(p).show("slow", function(){
                launchPage(p);
            });
        } else {
            $(p).hide("slow");
        }
    });
}

function launchPage(page){
    mixCategories = {};
    switch(page){
        case '#ContextModel':
            $('#ContextModelDisplay').show();
            graphInit();
            break;
        case '#ContextProviders':
            getListOfProviders();
            break;
        case '#ContextFactories':
            getListOfContextFactories();
            break;
        case '#ContextApplications':
            getListOfApplications();
            break;
    }
}

function clearPage(page){
    switch(page){
        case '#ContextModel':
            $('#ContextModelDisplay').hide();
            clearBox("ColumnInfo");
            clearBox("ContextNetwork");
            clearBox("DisplayModalBody");
            break;
        case '#ContextProviders':
            clearBox("providersSectionUI");
            clearBox("providersSection");
            break;
        case '#ContextFactories':
            clearBox("factoriesSectionUI");
            clearBox("factoriesSection");
            break;
        case '#ContextApplications':
            clearBox("applicationsSectionUI");
            clearBox("applicationsSection");
            break;
    }
}

/** MIX IT UP **/
function mixItUp_init(reference, UIref){

    if(mixitup != null){
        mixitup.mixItUp('destroy');
        mixitup = null;
    }

    mixItUp_initUI(UIref);

    mixitup = reference.mixItUp({
        layoutMode : 'list',
        easing : 'snap',
        animation: {
            duration: 200
        },
        load: {
            filter: reference.attr('data-mixitup-filter'),
            sort: 'name:asc'
        }
    });

    mixItUp_initSlideToggle(reference);
}

function mixItUp_initSlideToggle(reference){
    reference.find('.panel-heading').on('click', function(e) {
        var $this = $(this);
        $this.parent().find('.panel-body').slideToggle(200);
    });
}

function mixItUp_initUI(UIref){
    var input =  $("<input></input>").attr('id',"CustomFilter").attr('type',"text").attr('placeholder',"Custom Filter").attr('class',"search");
    input.appendTo(UIref);
    var button =  $("<button>Show all</button>").attr('class',"filter").attr('data-filter','all');
    button.appendTo(UIref);

    $("#CustomFilter").on("keydown",function mixItUp_filter(e) {
        if(e.keyCode == 13) {
        input = $(this).val();
        filter = "." + input;
        $.each(mixCategories, function(key, value){
            if(key.indexOf(input) > -1){
                filter = filter + ", ." + value;
            }
        });
            mixitup.mixItUp('filter', filter);
        }
    });
}

function mixItUp_addFilterCategory(panel, filterName){
    var filter = String.hashCode(filterName);
    mixCategories[filterName] = filter;
    panel.addClass(filter);
}

function mixItUp_addLink(targetPage, linkReference, filterName){
    switch(targetPage){
        case '#ContextModel':
            var menu = '#ContextModelMenu';
            break;
        case '#ContextProviders':
            var menu = '#ContextProvidersMenu';
            break;
        case '#ContextFactories':
            var menu = '#ContextFactoriesMenu';
            break;
        case '#ContextApplications':
            var menu = '#ContextApplicationsMenu';
            break;
    }

    linkReference.addClass('mixLink');

    linkReference.on("click",function(e){
        e.preventDefault();

        mixItUp_changeDefaultFilter(targetPage, "." + String.hashCode(filterName));
        changeActiveMenu(menu);
        changePageDisplay(targetPage);
    });
}

function mixItUp_changeDefaultFilter(targetPage, filter){
    $(targetPage).find('.mixSection').attr('data-mixitup-filter', filter);
}

/**USEFUL VARIOUS FUNCTIONS**/
function clearBox(elementID){

    console.log("Clear " + elementID);
    document.getElementById(elementID).innerHTML = "";
}

String.hashCode = function(string) {
  var hash = 0, i, chr, len;
  if (string.length === 0) return hash;
  for (i = 0, len = string.length; i < len; i++) {
    chr   = string.charCodeAt(i);
    hash  = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return "_" + hash;
};

function updateButton(button, state){
    $(button).attr('data-state', state);
    if(state == true){
        $(button).removeClass('btn-warning');
        $(button).html('true');
        $(button).addClass('btn-success');
    } else {
        $(button).removeClass('btn-success');
        $(button).html('false');
        $(button).addClass('btn-warning');
    }
}