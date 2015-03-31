
var options = {
    title: 'Electricity Consumption',
    legend: { position: 'bottom' },
    animation: {
        duration: 1000,
        easing: 'in'
    }
};

var data;

function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/electricityws");
    ws.onmessage = function (event) {
        var messageFromServer = event.data;
        var messageJson = JSON.parse(event.data)
        console.log(messageFromServer);
        console.log(messageFromServer.length);
        console.log(messageJson["date"]);

        var chart = new google.visualization.SteppedAreaChart(document.getElementById('chart_div'));

        data.addRow([messageJson["date"],parseInt(messageJson["value"])]);

        chart.draw(data, options);
    };

}

function init() {
    google.load("visualization", "1", {packages:["corechart"]});
    data = new google.visualization.DataTable();
    data.addColumn('string', 'x');
    data.addColumn('number', 'y');
    google.setOnLoadCallback(drawChart);

    function drawChart() {

        var t = $.get("/electricity/current",function(response) {
            data.addRow([response["date"],parseInt(response["value"])]);
        });

        var chart = new google.visualization.SteppedAreaChart(document.getElementById('chart_div'));

        chart.draw(data, options);
    }

    registerWebSocket();

}
