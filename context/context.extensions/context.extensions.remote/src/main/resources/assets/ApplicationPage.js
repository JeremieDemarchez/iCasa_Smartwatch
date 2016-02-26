function drawApplicationPanel(applicationId) {
    var $applicationsSection = $("#applicationsSection");

    var panel = $("<div></div>").attr('class',"mix panel panel-primary applicationPanel").attr('data-name', applicationId);;
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4>"+applicationId+"</h4>").attr('class',"panel-title");
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);
    panel.appendTo($applicationsSection);
    mixItUp_addFilterCategory(panel, applicationId);

    var panelBody = $("<div></div>").attr('class',"panel-body table-responsive");
    panelBody.appendTo(panel);
    return panelBody;
}

function drawApplicationContextDependencyPanel(applicationPanelBody,data) {

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

            mixItUp_addFilterCategory(applicationPanelBody.parent(), stringSpec);
            mixItUp_addLink('#ContextFactories', specification, stringSpec);
        });
    }

    tableBody.appendTo(tableImplem);
    tableImplem.appendTo(applicationPanelBody);
    return tableBody;
}

function getApplicationInstance(applicationId,applicationFactoryId,applicationInstanceId,appPanelBody){

    $.ajax({
        url: "/context/applications/"+applicationId+"/"+applicationFactoryId+"/"+applicationInstanceId,
        success: function(data){
            $.each(data,function(key,value){
                if(key == "requires"){
                    drawApplicationContextDependencyPanel(appPanelBody,value);
                }
            });
        },
        async: false
    });
}

function getApplicationInstances(applicationId,applicationFactoryId,appPanelBody){

    $.ajax({
        url: "/context/applications/"+applicationId+"/"+applicationFactoryId,
        success: function(data){
            $.each(data,function(key,value){
                getApplicationInstance(applicationId,applicationFactoryId,key,appPanelBody);
            });
        },
        async: false
    });
}
function getApplicationFactories(applicationId,appPanelBody){

    $.ajax({
        url: "/context/applications/"+applicationId,
        success: function(data){
            $.each(data,function(key,value){
                getApplicationInstances(applicationId,key,appPanelBody);
            });
        },
        async: false
    });
}

function getListOfApplications(){
    var $applicationsSection = $("#applicationsSection");
    var $applicationsSectionUI = $("#applicationsSectionUI");

    console.log("Get List of Applications");

    $.ajax({
        url: "/context/applications",
        success: function(data){
            $.each(data,function(key,value){
                var appPanelBody = drawApplicationPanel(key);
                getApplicationFactories(key,appPanelBody);
            });
        },
        async: false
    });

    mixItUp_init($applicationsSection, $applicationsSectionUI);
}