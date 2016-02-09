$(document).ready(function() {
    $('#ContextModelMenu').click(function(e){
        e.preventDefault();
        clickMenu(this);
        $('#ContextModel').show("slow");
    });
    $('#ContextProvidersMenu').click(function(e){
        e.preventDefault();
        clickMenu(this);
        $('#ContextProviders').show("slow");
        getListOfProviders();
    });
    $('#ContextFactoriesMenu').click(function(e){
        e.preventDefault();
        clickMenu(this);
        $('#ContextFactories').show("slow");
        getListOfContextFactories();
    });
    $('#ContextApplicationsMenu').click(function(e){
        e.preventDefault();
        clickMenu(this);
        $('#ContextApplications').show("slow");
        getListOfApplications();
    });

    function clickMenu(menu){
        $(".active").removeClass('active');
        $(menu).addClass('active');

        $('#ContextModel').hide("slow");
        $('#ContextProviders').hide("slow");
        $('#ContextFactories').hide("slow");
        $('#ContextApplications').hide("slow");

    }
});

function clearBox(elementID){

    console.log("Clear " + elementID);
    document.getElementById(elementID).innerHTML = "";
}