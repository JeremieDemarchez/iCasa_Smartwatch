$(document).ready(function() {

    graphInit(0);

    $('#ContextModelMenu').click(function(e){
        e.preventDefault();
        if ($(this).is($(".active"))){
            graphInit(0);
        } else {
            graphInit(800);
        }
        clickMenu(this, '#ContextModel');
    });
    $('#ContextProvidersMenu').click(function(e){
        e.preventDefault();
        clickMenu(this, '#ContextProviders');
        getListOfProviders();
    });
    $('#ContextFactoriesMenu').click(function(e){
        e.preventDefault();
        clickMenu(this,'#ContextFactories');
        getListOfContextFactories();
    });
    $('#ContextApplicationsMenu').click(function(e){
        e.preventDefault();
        clickMenu(this, '#ContextApplications');
        getListOfApplications();
    });

    function clickMenu(menu, page){
        var pages = ['#ContextModel', '#ContextProviders', '#ContextFactories', '#ContextApplications'];
        $(".active").removeClass('active');
        $(menu).addClass('active');
        $.each(pages,function(index, p){
            if(p == page) {
                $(p).show("slow");
            } else {
                $(p).hide("slow");
            }
        });
    }
});

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
  return hash;
};