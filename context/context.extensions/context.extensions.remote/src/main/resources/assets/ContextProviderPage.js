function drawProviderPanel(providerId,data){

    var providersSection = $("#providersSection");
    var providerHash = String.hashCode(providerId);

    var panel = $("<div></div>").attr('class',"panel panel-primary providerPanel").attr('id',"provider"+providerId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4></h4>").attr('class',"panel-title");
    var panelPanelTitleCollapsible = $("<a>"+providerId+"</a>").attr('data-toggle','collapse').attr('href',"#provider"+providerHash);

    panelPanelTitleCollapsible.appendTo(panelPanelTitle);
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);

    var div_collapse =  $("<div></div>").attr('class',"table-responsive collapse in").attr("id","provider"+providerHash);
    var table =  $("<table></table>").attr('class',"table");

    var tableHead =  $("<thead></thead>");
    var tableRHead =  $("<tr><th>Implementation</th><th>Enabled</th></tr>");
    tableRHead.appendTo(tableHead);

    tableHead.appendTo(table);
    var tableBody =  $("<tbody></tbody>");

    $.each(data,function(key,value){
        console.log("Draw For Each " + key + " value " + value);
        var row = $("<tr></tr>");
        var specification =  $("<td>"+key+"</td>").attr('class', "col-md-9");
        var status =  $("<td>"+value+"</td>").attr('class',"enabler btn col-md-1").attr('data-provider', providerId).attr('data-implem',key);
        updateButton(status, value);

        specification.appendTo(row);
        status.appendTo(row);
        row.appendTo(tableBody);
    });

    tableBody.appendTo(table);
    table.appendTo(div_collapse);
    div_collapse.appendTo(panel);
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