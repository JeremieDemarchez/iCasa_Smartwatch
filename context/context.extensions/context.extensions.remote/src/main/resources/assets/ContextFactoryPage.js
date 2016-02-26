function drawFactoryPanel(factoryId,data){
    var $factoriesSection = $("#factoriesSection");

    var panel = $("<div></div>").attr('class',"mix panel panel-primary factoryPanel").attr('data-name', factoryId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4>"+factoryId+"</h4>").attr('class',"panel-title");
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);
    mixItUp_addFilterCategory(panel, factoryId);

    var panelBody =  $("<div></div>").attr('class',"panel-body table-responsive");
    mixItUp_addLink('#ContextProviders', panelBody, factoryId);

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

        mixItUp_addFilterCategory(panel, key);
    });

    tableBody.appendTo(table);
    table.appendTo(panelBody);
    panelBody.appendTo(panel);
    panel.appendTo($factoriesSection);
}


function getContextFactory(factoryId){

    $.ajax({
        url: "/context/factories/"+factoryId,
        success: function(data){
            drawFactoryPanel(factoryId,data);

            $.each(data,function(key,value){
                console.log("For Each " + key + " value " + value);
            });
        },
        async: false
    });

}

function getListOfContextFactories(){
    var $factoriesSection = $("#factoriesSection");
    var $factoriesSectionUI = $("#factoriesSectionUI");

    console.log("Get List of Context Entity Type");

    $.ajax({
        url: "/context/factories",
        success: function(data){
            $.each(data,function(key,value){
                getContextFactory(key);
            });
        },
        async: false
    });
    mixItUp_init($factoriesSection, $factoriesSectionUI);
}



