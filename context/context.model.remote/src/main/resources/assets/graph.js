
var nodes, edges, network;

function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/temperature");
    ws.onmessage = function (event) {
        var messageFromServer = event.data;
        $("#temperature").html(messageFromServer);
    };

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
    network.on("selectEdge", function (params) {
        var edgeToUpdate =  params["edges"];
        console.log('selectEdge Event:', edgeToUpdate);
        edgeToUpdate.forEach(function(y){
            edges.update({id: y,label: y});
            var url = "/context/entities/"+y;
            var t = $.get("/context/entities",function(data) {
                console.log('Edge Event Get Response:', data);
            });
        });
    });
    network.on("deselectEdge", function (params) {
        var edgeToUpdate =  params["previousSelection"]["edges"];
        console.log('deselectEdge Event:', edgeToUpdate);
        edgeToUpdate.forEach(function(y){
            edges.update({id: y,label: ""});
        });
    });
    network.on("selectNode", function (params) {
        var nodeToUpdate =  params["nodes"];
        console.log('selectNode Event:', nodeToUpdate);
    });
    network.on("deselectNode", function (params) {
        var nodesToUpdate =  params["previousSelection"]["nodes"];
        console.log('deselectNode Event:', nodesToUpdate);
    });
}


function init() {
    //   registerWebSocket();
// create an array with nodes
    nodes = new vis.DataSet();
    edges = new vis.DataSet();
    var t = $.get("/context/entities",function(data) {
        var numberOfEntities = data.size;
        console.log(data.size);
        for(var i = 0;i<numberOfEntities;i ++){
            console.log(i);
            var entityId = "entity"+i;
            console.log(entityId);
            console.log(data[entityId]);
            var ContextEntityName = data[entityId];
            nodes.add({
                id: ContextEntityName,
                label: ContextEntityName
            });
        }
        draw();
    });

    var y = $.get("/context/relations",function(data) {
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
                arrows:'to'
            });
        }
        draw();
    });



}