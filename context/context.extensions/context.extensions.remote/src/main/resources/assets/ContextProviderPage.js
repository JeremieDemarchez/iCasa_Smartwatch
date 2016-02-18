function drawProviderPanel(providerId,dataImpl,dataRelation){

    var providersSection = $("#providersSection");
    var providerHash = String.hashCode(providerId);

    var panel = $("<div></div>").attr('class',"panel panel-primary providerPanel").attr('id',"provider"+providerId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+providerId+"</a>").attr('data-toggle','collapse').attr('href',"#provider"+providerHash);

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);

    var div_collapse =  $("<div></div>").attr('class',"table-responsive collapse").attr("id","provider"+providerHash);

    if(!$.isEmptyObject(dataImpl)){
        var tableImplem =  $("<table></table>").attr('class',"table");

        var tableHead =  $("<thead></thead>");
        var tableRHead =  $("<tr><th>Context Service Implementation</th><th>Enabled</th></tr>");
        tableRHead.appendTo(tableHead);

        tableHead.appendTo(tableImplem);
        var tableBody =  $("<tbody></tbody>");

        $.each(dataImpl,function(key,value){
            console.log("Draw For Each " + key + " value " + value);
            var row = $("<tr></tr>");
            var specification =  $("<td>"+key+"</td>").attr('class', "col-md-8");
            var status =  $("<td>"+value+"</td>").attr('class',"enabler btn col-md-2").attr('data-provider', providerId).attr('data-implem',key);
            updateButton(status, value);

            specification.appendTo(row);
            status.appendTo(row);
            row.appendTo(tableBody);
        });

        tableBody.appendTo(tableImplem);
        tableImplem.appendTo(div_collapse);
    }

    if(!$.isEmptyObject(dataRelation)){
        var tableRelation =  $("<table></table>").attr('class',"table");

        var tableHead =  $("<thead></thead>");
        var tableRHead =  $("<tr><th>Relation</th><th>Enabled</th></tr>");
        tableRHead.appendTo(tableHead);

        tableHead.appendTo(tableRelation);
        var tableBody =  $("<tbody></tbody>");

        $.each(dataRelation,function(key,value){
            console.log("Draw For Each " + key + " value " + value);
            var row = $("<tr></tr>");
            var specification =  $("<td>"+key+"</td>").attr('class', "col-md-8");
            var status =  $("<td>"+value+"</td>").attr('class',"enabler btn col-md-2").attr('data-provider', providerId).attr('data-implem',key);
            updateButton(status, value);

            specification.appendTo(row);
            status.appendTo(row);
            row.appendTo(tableBody);
        });

        tableBody.appendTo(tableRelation);
        tableRelation.appendTo(div_collapse);
    }


    div_collapse.appendTo(panel);
    panel.appendTo(providersSection);
}

function getProviderPanel(providerId){

    t = $.get("/context/providers/"+providerId,function(dataImpl) {
        t = $.get("/context/providers/relations/"+providerId,function(dataRelation) {
            console.log('Provider ', providerId);
            console.log('Impl ', dataImpl);
            console.log('Rel ', dataRelation);
            drawProviderPanel(providerId,dataImpl,dataRelation);
        });
    });
}

function getListOfProviders(){

    clearBox("providersSection");
    console.log("Get List of Provider");
    t = $.get("/context/providers",function(data) {
        $.each(data,function(key,value){
            getProviderPanel(key);
        });
    });
}

$(document).ready(function() {
    $('#providersSection').on('click', '.enabler', function(e) {
        e.preventDefault();

        var providerId = $(this).attr('data-provider');
        var implem = $(this).attr('data-implem');
        var state = $(this).attr('data-state');
        var button = this;
        t = $.post("/context/providers/"+providerId+"/"+implem+"/"+ (state == 'false'), function(data) {
            $.each(data,function(key, value){
                updateButton(button, value);
            });
        });


    });
});