function drawApplicationPanel(applicationId) {
    var applicationSection = $("#applicationsSection");
    var applicationHash = String.hashCode(applicationId);
    var panel = $("<div></div>").attr('class',"panel panel-primary applicationPanel").attr('id',"applicationPrimary"+applicationId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+applicationId+"</a>").attr('data-toggle','collapse').attr('href',"#application"+applicationHash);

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);
    panel.appendTo(applicationSection);

    var div_collapse =  $("<div></div>").attr('class',"table-responsive collapse").attr("id","application"+applicationHash);
    div_collapse.appendTo(panel);
    return div_collapse;
}

function drawApplicationContextDependencyPanel(applicationPanel,data) {

    var tableImplem =  $("<table></table>").attr('class',"table");

    var tableHead =  $("<thead></thead>");
    var tableRHead =  $("<tr><th>Context Service Dependency</th><th>Optionnal</th><th>State</th></tr>");
    tableRHead.appendTo(tableHead);

    tableHead.appendTo(tableImplem);
    var tableBody =  $("<tbody></tbody>");

    if(!$.isEmptyObject(data)){
        $.each(data,function(key,value){
            console.log("Draw For Each " + key + " value " + value);
            var stringSpec = value["service"];
            var stringOptionnal = value["optional"];
            var stringState = value["state"];
            var row = $("<tr></tr>");
            var specification =  $("<td>"+stringSpec+"</td>").attr('class', "col-md-6");
            var optionnal =  $("<td>"+stringOptionnal+"</td>").attr('class', "col-md-3");
            var state =  $("<td>"+stringState+"</td>").attr('class', "col-md-3");


            specification.appendTo(row);
            optionnal.appendTo(row);
            state.appendTo(row);
            row.appendTo(tableBody);
        });
    }

    tableBody.appendTo(tableImplem);
    tableImplem.appendTo(applicationPanel);
    return tableBody;
}

function getApplicationInstance(applicationId,applicationFactoryId,applicationInstanceId,appPanel){

    t = $.get("/context/applications/"+applicationId+"/"+applicationFactoryId+"/"+applicationInstanceId,function(data) {
        $.each(data,function(key,value){
            if(key == "requires"){
                drawApplicationContextDependencyPanel(appPanel,value);
            }

        });
    });
}

function getApplicationInstances(applicationId,applicationFactoryId,appPanel){

    t = $.get("/context/applications/"+applicationId+"/"+applicationFactoryId,function(data) {
        $.each(data,function(key,value){
            getApplicationInstance(applicationId,applicationFactoryId,key,appPanel);
        });
    });
}
function getApplicationFactories(applicationId,appPanel){

    t = $.get("/context/applications/"+applicationId,function(data) {
        $.each(data,function(key,value){
            getApplicationInstances(applicationId,key,appPanel);
        });
    });
}

function getListOfApplications(){

    clearBox("applicationsSection");
    console.log("Get List of Applications");
    t = $.get("/context/applications",function(data) {
        $.each(data,function(key,value){
            var appPanel = drawApplicationPanel(key);
            getApplicationFactories(key,appPanel);
        });
    });
}