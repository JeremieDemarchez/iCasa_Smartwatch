
var nodes, edges, network;

function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/temperature");
    ws.onmessage = function (event) {
        var messageFromServer = event.data;
        $("#temperature").html(messageFromServer);
    };

}


function addNode() {
    try {
        nodes.add({
            id: document.getElementById('node-id').value,
            label: document.getElementById('node-label').value
        });
    }
    catch (err) {
        alert(err);
    }
}


function init() {
    registerWebSocket();
// create an array with nodes
    var t = $.get("/context/entities",function(data) {
        $("#targetTemperature").text(data);

        $('#tempSlider').attr('data-slider-value',data);

    });
    nodes = new vis.DataSet([
        {id: 1, label: 'Node 1'},
        {id: 2, label: 'Node 2'},
        {id: 3, label: 'Node 3'},
        {id: 4, label: 'Node 4'},
        {id: 5, label: 'Node 5'}
    ]);

    // create an array with edges
    edges = new vis.DataSet([
        {from: 1, to: 3},
        {from: 1, to: 2},
        {from: 2, to: 4},
        {from: 2, to: 5}
    ]);

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