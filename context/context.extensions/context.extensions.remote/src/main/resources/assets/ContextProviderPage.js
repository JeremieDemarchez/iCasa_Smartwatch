function drawProviderPanel(providerId,data){

    var providersSection = $("#providersSection");

    var panel = $("<div></div>").attr('class',"panel panel-primary providerPanel").attr('id',"provider"+providerId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+providerId+"</a>").attr('data-toggle','collapse').attr('href',"#provider"+providerId+"table");

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);

    var table =  $("<table></table>").attr('class',"table  collapse in").attr("id","provider"+providerId+"table");

    var tableHead =  $("<thead></thead>");
    var tableRHead =  $("<tr><th>Implementation</th><th>Enabled</th></tr>");
    tableRHead.appendTo(tableHead);

    tableHead.appendTo(table);
    var tableBody =  $("<tbody></tbody>");

    $.each(data,function(key,value){
        console.log("Draw For Each " + key + " value " + value);
        var row = $("<tr></tr>");
        var specification =  $("<td>"+key+"</td>");
        if(value == true) {
            var status =  $("<td>"+value+"</td>").attr('class',"enab btn btn-success");
        } else {
            var status =  $("<td>"+value+"</td>").attr('class',"disab btn btn-warning");
        }

        specification.appendTo(row);
        status.appendTo(row);
        row.appendTo(tableBody);
    });

    tableBody.appendTo(table);


    table.appendTo(panel);

    panel.appendTo(providersSection);
}

function getProvider(providerId){

    t = $.get("/context/providers/"+providerId,function(data) {
        drawProviderPanel(providerId,data);
        $.each(data,function(key,value){
            console.log("For Each " + key + " value " + value);
        });
    });
}

function getListOfProviders(){

    clearBox("providersSection");
    console.log("Get List of Provider");
    t = $.get("/context/providers",function(data) {
        $.each(data,function(key,value){
            getProvider(key)
        });
    });
}

$(document).ready(function() {
    $('#providersSection').on('click', '.enab', function(e) {
        e.preventDefault();
        $(this).removeClass('btn-success');
        $(this).removeClass('enab');
        $(this).html('false');
        $(this).addClass('disab');
        $(this).addClass('btn-warning');

    });

    $('#providersSection').on('click', '.disab', function(e) {
            e.preventDefault();
            $(this).removeClass('btn-warning');
            $(this).removeClass('disab');
            $(this).html('true');
            $(this).addClass('enab');
            $(this).addClass('btn-success');

    });
});