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

    var panelFactory = $("<div></div>").attr('class',"panel panel-info").attr('id',"factoryPanel"+elementId);
    var panelFactoryHeading =  $("<div>Factory</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#factoryPanelInfo");
    var panelStates = $("<div></div>").attr('class',"panel panel-success").attr('id',"statesPanel"+elementId);
    var panelStatesHeading =  $("<div>State</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#statesPanelInfo");
    var panelRelations = $("<div></div>").attr('class',"panel panel-warning").attr('id',"relationsPanel"+elementId);
    var panelRelationsHeading =  $("<div>Relations</div>").attr('class',"panel-heading").attr('data-toggle','collapse').attr('href',"#relationsPanelInfo");

    panelFactoryHeading.appendTo(panelFactory);
    panelStatesHeading.appendTo(panelStates);
    panelRelationsHeading.appendTo(panelRelations);

    panelHeading.appendTo(panel);
    panelFactory.appendTo(panel);
    panelStates.appendTo(panel);
    panelRelations.appendTo(panel);
    panel.appendTo(columnInfo);
}

function removeNodeEntityPanel(elementId){
    $("#nodeEntityPanel"+elementId).remove();
}

function addNodeFactoryPanel(elementId,data){
    var panelFactory = $("#factoryPanel"+elementId);
    var div_collapse =  $("<div></div>").attr('class',"collapse in").attr("id","factoryPanelInfo");
    var panelBody =  $("<div></div>").attr('class',"panel-body");
    var factoryGroupList =  $("<ul></ul>").attr('class',"list-group");
    $.each(data,function(key,val){
        console.log("KEY : " + key + " , VAL : " + val);
        var factoryGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item scroll-txt");
        factoryGroupListItem.appendTo(factoryGroupList);
        factoryGroupList.appendTo(panelBody);
    });

    /** Append all to panel**/
    panelBody.appendTo(div_collapse);
    div_collapse.appendTo(panelFactory);
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

//function addNodeStateExtensionPanel(elementId,data){
//    var panelExtensionState = $("#stateExtensionPanel"+elementId);
//    var panelBody =  $("<div></div>").attr('class',"panel-body");
//    var stateGroupList =  $("<ul></ul>").attr('class',"list-group");
//    $.each(data,function(key,val){
//        console.log("KEY : " + key + " , VAL : " + val);
//        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item");
//        var stateGroupListValue =  $("<span>"+val+"</span>").attr('class',"badge");
//        stateGroupListValue.appendTo(stateGroupListItem);
//        stateGroupListItem.appendTo(stateGroupList);
//        stateGroupList.appendTo(panelBody);
//    });
//
//    /** Append all to panel**/
//    panelBody.appendTo(panelExtensionState);
//}



//function addRelationStatePanel(elementId,data){
//    $("#relationStatePanel"+elementId).remove();
//    var panel = $("<div></div>").attr('class',"panel panel-primary").attr('id',"relationStatePanel"+elementId);
//    var panelHeading =  $("<div>Relation State</div>").attr('class',"panel-heading");
//    var panelBody =  $("<div></div>").attr('class',"panel-body");
//    var stateGroupList =  $("<ul></ul>").attr('class',"list-group");
//    $.each(data,function(key,val){
//        console.log("KEY : " + key + " , VAL : " + val);
//        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item");
//        var stateGroupListValue =  $("<span>"+val+"</span>").attr('class',"badge");
//        stateGroupListValue.appendTo(stateGroupListItem);
//        stateGroupListItem.appendTo(stateGroupList);
//        stateGroupList.appendTo(panelBody);
//    });
//
//    /** Append all to panel**/
//    panelHeading.appendTo(panel);
//    panelBody.appendTo(panel);
//    panel.appendTo(columnInfo);
//}
//
//function removeRelationStatePanel(elementId){
//    $("#relationStatePanel"+elementId).remove();
//}

function graphDraw(){
    // create a network
    var container = document.getElementById('ContextNetwork');

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
            createNodeEntityPanel(y);
            var urlStates = "/context/entities/" + y;
            var urlFactory = "/context/entities/factory/" + y;



            var t = $.get(urlFactory, function (data) {
                addNodeFactoryPanel(y,data);
            });

            var t = $.get(urlStates, function (data) {
                addNodeStatesPanel(y,data);
            });

       /**     var urlExtensions = "/context/entities/" + y+"/extensions";
            var t = $.get(urlExtensions, function (data) {
                addNodeStateExtensionPanel(y,data);
            });**/
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
// create an array with nodes
    clearBox("ColumnInfo");
    clearBox("ContextNetwork");

    nodes = new vis.DataSet();
    edges = new vis.DataSet();
    columnInfo = $("#ColumnInfo");

    var t = $.get("/context/entities",function(data) {
        $.each(data,function(key,value){
            nodes.add({
                id: key,
                label: key
            });
        });
        var timer = window.setTimeout(graphDraw, time);
//        graphDraw();
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
        graphDraw();
    });**/
}

