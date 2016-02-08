function clearBox(elementID){

    console.log("Clear " + elementID);
    document.getElementById(elementID).innerHTML = "";
}

function drawFactoryPanel(factoryId,data){

    var factorySection = $("#factoriesSection");

    var panel = $("<div></div>").attr('class',"panel panel-primary factoryPanel").attr('id',"factory"+factoryId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+factoryId+"</a>").attr('data-toggle',"collapse").attr('href',"#factory"+factoryId+"list");

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);

    var panelCollapse =  $("<div></div>").attr('class',"panel-collapse collapse in").attr("id","factory"+factoryId+"list");

    var listBody =  $("<ul></ul>").attr('class',"list-group");

    $.each(data,function(key,value){
        console.log("Draw For Each " + key + " value " + value);
        var row = $("<li>"+key+"</li>").attr('class',"list-group-item");


        row.appendTo(listBody);
    });

    listBody.appendTo(panelCollapse);


    panelCollapse.appendTo(panel);

    panel.appendTo(factorySection);
}


function getContextFactory(factoryId){

    t = $.get("/context/factories/"+factoryId,function(data) {
        drawFactoryPanel(factoryId,data);

        $.each(data,function(key,value){
            console.log("For Each " + key + " value " + value);
        });
    });
}

function getListOfContextFactories(){

    clearBox("factoriesSection");
    console.log("Get List of Context Entity Type");
    t = $.get("/context/factories",function(data) {
        $.each(data,function(key,value){
            getContextFactory(key)
        });
    });
}

