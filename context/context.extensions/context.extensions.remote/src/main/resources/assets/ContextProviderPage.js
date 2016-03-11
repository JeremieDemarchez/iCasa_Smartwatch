function drawProviderPanel(providerId,dataImpl,dataRelation){
    var $providersSection = $("#providersSection");

    var panel = $("<div></div>").attr('class',"mix panel panel-primary providerPanel").attr('data-name', providerId);
    var panelHeading =  $("<div></div>").attr('class',"panel-heading");
    var panelPanelTitle = $("<h4>"+providerId+"</h4>").attr('class',"panel-title");
    panelPanelTitle.appendTo(panelHeading);
    panelHeading.appendTo(panel);
    mixItUp_addFilterCategory(panel, providerId);

    var panelBody =  $("<div></div>").attr('class',"panel-body table-responsive");
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
            specification.appendTo(row);
            status.appendTo(row);
            row.appendTo(tableBody);

            updateButton(status, value);
            mixItUp_addFilterCategory(panel, key);
            mixItUp_addLink('#ContextFactories', specification, key);
        });

        tableBody.appendTo(tableImplem);
        tableImplem.appendTo(panelBody);
    }

    //TODO: uncomment
//    if(!$.isEmptyObject(dataRelation)){
//        var tableRelation =  $("<table></table>").attr('class',"table");
//        var tableHead =  $("<thead></thead>");
//        var tableRHead =  $("<tr><th>Relation</th><th>Enabled</th></tr>");
//        tableRHead.appendTo(tableHead);
//        tableHead.appendTo(tableRelation);
//
//        var tableBody =  $("<tbody></tbody>");
//        $.each(dataRelation,function(key,value){
//            console.log("Draw For Each " + key + " value " + value);
//            var row = $("<tr></tr>");
//            var specification =  $("<td>"+key+"</td>").attr('class', "col-md-8");
//            var status =  $("<td>"+value+"</td>").attr('class',"enabler btn col-md-2").attr('data-provider', providerId).attr('data-implem',key);
//            updateButton(status, value);
//            specification.appendTo(row);
//            status.appendTo(row);
//            row.appendTo(tableBody);
//
//            mixItUp_addFilterCategory(panel, key);
//        });
//
//        tableBody.appendTo(tableRelation);
//        tableRelation.appendTo(panelBody);
//    }

    panelBody.appendTo(panel);
    panel.appendTo($providersSection);
}

function getProviderPanel(providerId){

    $.ajax({
        url: "/context/providers/"+providerId,
        success: function(dataImpl) {
            $.ajax({
                url: "/context/providers/relations/"+providerId,
                success: function(dataRelation) {
                    console.log('Provider ', providerId);
                    console.log('Impl ', dataImpl);
                    console.log('Rel ', dataRelation);
                    //TODO: remove if
                    if(!$.isEmptyObject(dataImpl)){
                        drawProviderPanel(providerId,dataImpl,dataRelation);
                    }
                },
                async: false
            });
        },
        async: false
    });
}

function getListOfProviders(){
    var $providersSection = $("#providersSection");
    var $providersSectionUI = $("#providersSectionUI");

    console.log("Get List of Provider");
    $.ajax({
        url: "/context/providers",
        success: function(data){
            $.each(data,function(key,value){
                getProviderPanel(key);
            });
        },
        async: false
    });
    mixItUp_init($providersSection, $providersSectionUI);
}

$(document).ready(function() {
    var $providersSection = $("#providersSection");

    $providersSection.on('click', '.enabler', function(e) {
        e.preventDefault();

        var providerId = $(this).attr('data-provider');
        var implem = $(this).attr('data-implem');
        var state = $(this).attr('data-state');
        var button = this;
        $.post("/context/providers/"+providerId+"/"+implem+"/"+ (state == 'false'), function(data) {
            $.each(data,function(key, value){
                updateButton(button, value);
            });
        });
    });
});