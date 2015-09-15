
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
    var options = {};

    // initialize your network!
    network = new vis.Network(container, data, options);
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
            edges.add({
                id: "relation"+i+"name",
                from: data["relation"+i+"source"],
                to: data["relation"+i+"end"]
            });
        }
        draw();
    });



}