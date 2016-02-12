var nodes, edges, network, columnInfo;

//function registerWebSocket() {
//    var ws = $.easyWebSocket("ws://" + window.location.host + "/temperature");
//    ws.onmessage = function (event) {
//        var messageFromServer = event.data;
//        $("#temperature").html(messageFromServer);
//    };
//
//}


function createNodeEntityPanel(elementId){
    $("#nodeEntityPanel"+elementId).remove();
    var panel = $("<div></div>").attr('class',"panel panel-primary").attr('id',"nodeEntityPanel"+elementId);
    var panelHeading =  $("<div>Entity : "+elementId+"</div>").attr('class',"panel-heading");

    var panelServices = $("<div></div>").attr('class',"panel panel-info").attr('id',"servicesPanel"+elementId);
    var panelServicesHeading =  $("<div>Context Services</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#servicesPanelInfo");
    var panelStates = $("<div></div>").attr('class',"panel panel-success").attr('id',"statesPanel"+elementId);
    var panelStatesHeading =  $("<div>States</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#statesPanelInfo");
    var panelRelations = $("<div></div>").attr('class',"panel panel-warning").attr('id',"relationsPanel"+elementId);
    var panelRelationsHeading =  $("<div>Relations</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#relationsPanelInfo");

    panelServicesHeading.appendTo(panelServices);
    panelStatesHeading.appendTo(panelStates);
    panelRelationsHeading.appendTo(panelRelations);

    panelHeading.appendTo(panel);
    panelServices.appendTo(panel);
    panelStates.appendTo(panel);
    panelRelations.appendTo(panel);
    panel.appendTo(columnInfo);
}

function removeNodeEntityPanel(elementId){
    $("#nodeEntityPanel"+elementId).remove();
}

function addNodeServicesPanel(elementId,data){
    var panelServices = $("#servicesPanel"+elementId);
    var div_collapse =  $("<div></div>").attr('class',"collapse in").attr("id","servicesPanelInfo");
    var panelBody =  $("<div></div>").attr('class',"panel-body");
    var servicesGroupList =  $("<ul></ul>").attr('class',"list-group");
    $.each(data,function(key,val){
        console.log("KEY : " + key + " , VAL : " + val);
        var servicesGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item scroll-txt");
        servicesGroupListItem.appendTo(servicesGroupList);
        servicesGroupList.appendTo(panelBody);
    });

    /** Append all to panel**/
    panelBody.appendTo(div_collapse);
    div_collapse.appendTo(panelServices);
}

function addNodeStatesPanel(elementId,data){
    var panelState = $("#statesPanel"+elementId);
    var div_collapse =  $("<div></div>").attr('class',"collapse in").attr("id","statesPanelInfo");
    var panelBody =  $("<div></div>").attr('class',"panel-body");
    var stateGroupList =  $("<ul></ul>").attr('class',"list-group");
    $.each(data,function(key,val){
        console.log("KEY : " + key + " , VAL : " + val);
        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item");
        var stateGroupListValue =  $("<span>"+val+"</span>").attr('class',"badge");
        stateGroupListValue.appendTo(stateGroupListItem);
        stateGroupListItem.appendTo(stateGroupList);
        stateGroupList.appendTo(panelBody);
    });

    /** Append all to panel**/
    panelBody.appendTo(div_collapse);
    div_collapse.appendTo(panelState);
}

function addNodeRelationsPanel(elementId,data){
var panelState = $("#relationsPanel"+elementId);
    var div_collapse =  $("<div></div>").attr('class',"collapse in").attr("id","relationsPanelInfo");
    var panelBody =  $("<div></div>").attr('class',"panel-body");
    var stateGroupList =  $("<ul></ul>").attr('class',"list-group");
    $.each(data,function(key,val){
        console.log("KEY : " + key + " , VAL : " + val);
        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item");
        var stateGroupListValue =  $("<span>"+val+"</span>").attr('class',"badge");
        stateGroupListValue.appendTo(stateGroupListItem);
        stateGroupListItem.appendTo(stateGroupList);
        stateGroupList.appendTo(panelBody);
    });

    /** Append all to panel**/
    panelBody.appendTo(div_collapse);
    div_collapse.appendTo(panelState);
}

function graphDraw(){
    // create a network
    var container = document.getElementById('ContextNetwork');

    // provide the data in the vis format
    var data = {
        nodes: nodes,
        edges: edges
    };

    var options = {
        nodes: {
            shape: 'circle',
            scaling: {
                min: 10,
                max: 30,
                label: {
                    min: 10,
                    max: 30,
                    drawThreshold: 12,
                    maxVisible: 30
                }
            }
        }, edges:{
            smooth: {forceDirection: 'none'}
        }, interaction:{
            hover: true,
            navigationButtons: true
        },
        physics: {
            forceAtlas2Based: {springLength: 100},
            minVelocity: 0.75,
            solver: 'forceAtlas2Based'
        }
    };
    network = new vis.Network(container, data, options);

    network.on("selectEdge", function (params) {
        var edgeToUpdate =  params["edges"];
        console.log('selectEdge Event:', edgeToUpdate);
        edgeToUpdate.forEach(function(y){
            console.log("Edge Name " + edges.get(y).name);
            edges.update({id: y, label: edges.get(y).name});
        });
    });

    network.on("deselectEdge", function (params) {
        var edgeToRemove =  params["previousSelection"]["edges"];
        console.log('deselectEdge Event:', edgeToRemove);
        edgeToRemove.forEach(function(y){
            edges.update({id: y, label: ""});
        });
    });

    network.on("selectNode", function (params) {
        var nodeToUpdate =  params["nodes"];
        console.log('selectNode Event:', nodeToUpdate);
        nodeToUpdate.forEach(function(y) {
            createNodeEntityPanel(y);
            var urlStates = "/context/entities/" + y;
            var urlServices = "/context/entities/" + y + "/services";
            var urlRelations = "/context/entities/" + y +"/relations";

            var t = $.get(urlServices, function (data) {
                addNodeServicesPanel(y,data);
            });

            var t = $.get(urlStates, function (data) {
                addNodeStatesPanel(y,data);
            });


            var t = $.get(urlRelations, function (data) {
                addNodeRelationsPanel(y,data);
            });
        });
    });

    network.on("deselectNode", function (params) {
        var nodesToRemove =  params["previousSelection"]["nodes"];
        nodesToRemove.forEach(function(y) {
            removeNodeEntityPanel(y);
        });
    });
}


function graphInit(time) {
    //   registerWebSocket();
    clearBox("ColumnInfo");
    clearBox("ContextNetwork");

    nodes = new vis.DataSet();
    edges = new vis.DataSet();
    columnInfo = $("#ColumnInfo");

    var t = $.get("/context/entities",function(data) {
        $.each(data,function(key,value){
            console.log('Node Id:', key);
            nodes.add({
                id: key,
                label: key
            });
        });
    });

    var y = $.get("/context/relations",function(data) {
        var numberOfRelations = data.size;
        for(var i = 0;i<numberOfRelations;i ++){
            var name = data["relation"+i+"name"];
            var source = data["relation"+i+"source"];
            var target = data["relation"+i+"target"];
            if ((null != nodes.get(source)) && (null != nodes.get(target))){
                var edgeId = name+source+target;
                edges.add({
                    id: edgeId,
                    from: source,
                    to: target,
                    arrows:'to',
                    name: name,
                    font: {align: 'horizontal'}
                });
            }
        }
    });

    var timer = window.setTimeout(graphDraw, time);
}

