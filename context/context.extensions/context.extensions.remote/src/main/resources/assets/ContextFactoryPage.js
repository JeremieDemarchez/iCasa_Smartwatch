function drawFactoryPanel(factoryId,data){

    var factorySection = $("#factoriesSection");
    var factoryHash = String.hashCode(factoryId);

    var panel = $("<div></div>").attr('class',"panel panel-primary factoryPanel").attr('id',"factory"+factoryId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+factoryId+"</a>").attr('data-toggle',"collapse").attr('href',"#factory"+factoryHash);

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);

    var div_collapse =  $("<div></div>").attr('class',"table-responsive collapse in").attr("id","factory"+factoryHash);
    var table =  $("<table></table>").attr('class',"table  table-striped");

    var tableHead =  $("<thead></thead>");
    var tableRHead =  $("<tr><th>Context Service</th></tr>");
    tableRHead.appendTo(tableHead);

    tableHead.appendTo(table);
    var tableBody =  $("<tbody></tbody>");

    $.each(data,function(key,value){
        console.log("Draw For Each " + key + " value " + value);
        var row = $("<tr></tr>");
        var contextService =  $("<td>"+key+"</td>");

        contextService.appendTo(row);
        row.appendTo(tableBody);
    });

    tableBody.appendTo(table);
    table.appendTo(div_collapse);
    div_collapse.appendTo(panel);
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



