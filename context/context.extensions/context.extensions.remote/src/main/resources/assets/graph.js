
var nodes, edges, network,stateColumn;

function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/temperature");
    ws.onmessage = function (event) {
        var messageFromServer = event.data;
        $("#temperature").html(messageFromServer);
    };

}

function createNodeStatePanel(elementId){
    $("#nodeStatePanel"+elementId).remove();
    var panel = $("<div></div>").attr('class',"panel panel-primary").attr('id',"nodeStatePanel"+elementId);
    var panelHeading =  $("<div>Node : "+elementId+"</div>").attr('class',"panel-heading");
    var panelState = $("<div></div>").attr('class',"panel panel-info").attr('id',"statePanel"+elementId);
    var panelStateHeading =  $("<div>State</div>").attr('class',"panel-heading");
    var panelExtensionState = $("<div></div>").attr('class',"panel panel-warning").attr('id',"stateExtensionPanel"+elementId);
    var panelExtensionStateHeading =  $("<div>Extension</div>").attr('class',"panel-heading");
    panelExtensionStateHeading.appendTo(panelExtensionState);
    panelStateHeading.appendTo(panelState);
    panelHeading.appendTo(panel);
    panelState.appendTo(panel);
    panelExtensionState.appendTo(panel);
    panel.appendTo(stateColumn);
}

function addNodeStatePanel(elementId,data){
    var panelState = $("#statePanel"+elementId);
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
    panelBody.appendTo(panelState);

}

function addNodeStateExtensionPanel(elementId,data){
    var panelExtensionState = $("#stateExtensionPanel"+elementId);
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
    panelBody.appendTo(panelExtensionState);
}

function removeNodeStatePanel(elementId){
    $("#nodeStatePanel"+elementId).remove();
}

function addRelationStatePanel(elementId,data){
    $("#relationStatePanel"+elementId).remove();
    var panel = $("<div></div>").attr('class',"panel panel-primary").attr('id',"relationStatePanel"+elementId);
    var panelHeading =  $("<div>Relation State</div>").attr('class',"panel-heading");
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
    panelHeading.appendTo(panel);
    panelBody.appendTo(panel);
    panel.appendTo(stateColumn);
}

function removeRelationStatePanel(elementId){
    $("#relationStatePanel"+elementId).remove();
}

function draw(){
    // create a network
    var container = document.getElementById('mynetwork');

    // provide the data in the vis format
    var data = {
        nodes: nodes,
        edges: edges
    };

    var options = {interaction:{hover:true}};
    network = new vis.Network(container, data, options);

 /**   network.on("selectEdge", function (params) {
        var edgeToUpdate =  params["edges"];
        console.log('selectEdge Event:', edgeToUpdate);
        edgeToUpdate.forEach(function(y){
            console.log("Edge Name " + edges.get(y).name);
            edges.update({id: y,label: edges.get(y).name});
            var url = "/context/relations/"+y;
            var t = $.get(url,function(data) {
                addRelationStatePanel(y,data);
            });
        });
    });**/

/**    network.on("deselectEdge", function (params) {
        var edgeToRemove =  params["previousSelection"]["edges"];
        console.log('deselectEdge Event:', edgeToRemove);
        edgeToRemove.forEach(function(y){
            edges.update({id: y,label: ""});
            removeRelationStatePanel(y);
        });
    });**/

    network.on("selectNode", function (params) {
        var nodeToUpdate =  params["nodes"];
        console.log('selectNode Event:', nodeToUpdate);
        nodeToUpdate.forEach(function(y) {
            createNodeStatePanel(y);
            var urlState = "/context/entities/" + y;

            var t = $.get(urlState, function (data) {
                addNodeStatePanel(y,data);
            });

       /**     var urlExtensions = "/context/entities/" + y+"/extensions";
            var t = $.get(urlExtensions, function (data) {
                addNodeStateExtensionPanel(y,data);
            });**/
        });
    });

  /**  network.on("deselectNode", function (params) {
        var nodesToRemove =  params["previousSelection"]["nodes"];
        nodesToRemove.forEach(function(y) {
            removeNodeStatePanel(y);
        });
    });**/
}


function init() {
    //   registerWebSocket();
// create an array with nodes
    nodes = new vis.DataSet();
    edges = new vis.DataSet();
    stateColumn = $("#stateColumn");

    var t = $.get("/context/entities",function(data) {
        $.each(data,function(key,value){
            nodes.add({
                id: key,
                label: key
            });
        });
        draw();
    });

   /** var y = $.get("/context/relations",function(data) {
        var numberOfRelations = data.size;
        console.log(data.size);
        for(var i = 0;i<numberOfRelations;i ++){
            console.log(data["relation"+i+"name"]);
            console.log(data["relation"+i+"source"]);
            console.log(data["relation"+i+"end"]);
            var nodeId = data["relation"+i+"name"]+data["relation"+i+"source"]+data["relation"+i+"end"];
            edges.add({
                id: nodeId,
                from: data["relation"+i+"source"],
                to: data["relation"+i+"end"],
                arrows:'to',
                name: data["relation"+i+"name"]
            });
        }
        draw();
    });**/
}

