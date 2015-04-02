
var options = {
    title: 'Electricity Consumption',
    vAxis: {title: 'Electricity Consumption in Watt'},
    animation: {
        duration: 1000,
        easing: 'in'
    }
};

var data;
var zoneDataMap;
function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/electricityws");
    ws.onmessage = function (event) {
        var messageFromServer = event.data;
        var messageJson = JSON.parse(event.data);
        if(messageJson["zone"]=="global"){
            var chart = new google.visualization.SteppedAreaChart(document.getElementById('global_chart'));

            data.addRow([messageJson["date"],parseInt(messageJson["value"])]);

            chart.draw(data, options);

        }else {
            var zone = messageJson["zone"];
            var date = messageJson["date"];
            var value = parseInt(messageJson["value"]);
            var id = '#'+zone;
            console.log("id " + id);

            if(value == -1){
                console.log(" Destruction " + zone + " value " + 0);
            }else if(value == -2){
                if($(id).length < 1 ){

                    $("<div></div>").attr('id',zone).appendTo($("#chart_container"));

                    var zonechart = new google.visualization.ColumnChart(document.getElementById(zone));
                    var particularOptions = {
                        title: 'Electricity Consumption in '+zone,
                        vAxis: {title: 'Electricity Consumption in Watt',minValue:0, maxValue:10000},
                        animation: {
                            duration: 5000,
                            easing: 'out'
                        }
                    };
                    var zoneData = google.visualization.arrayToDataTable([
                        ["Zone", "Consumption", { role: "style" } ],
                        [zone,0, 'gold'] ]);
                    zoneDataMap.set(zone,zoneData);
                    zonechart.draw(zoneData, particularOptions);
                    console.log(" Creation " + zone + " value " + 0);
                }
            }else{
                var zonechartToUpdate;
                var particularOptions = {
                    title: 'Electricity Consumption in '+zone,
                    vAxis: {title: 'Electricity Consumption in Watt',minValue:0, maxValue:10000},
                    animation: {
                        duration: 5000,
                        easing: 'out'
                    }
                };
                if($(id).length < 1 ){
                    $("<div></div>").attr('id',zone).appendTo($("#chart_container"));

                    var zonechartToUpdate = new google.visualization.ColumnChart(document.getElementById(zone));

                    var zoneData = google.visualization.arrayToDataTable([
                        ["Zone", "Consumption", { role: "style" } ],
                        [zone,0, 'gold'] ]);
                    zoneDataMap.set(zone,zoneData);
                    zonechartToUpdate.draw(zoneData, particularOptions);
                    console.log(" Creation " + zone + " value " + 0);
                }else{
                    zonechartToUpdate= new google.visualization.ColumnChart(document.getElementById(zone));
                }
                var dataToUpdate = zoneDataMap.get(zone);
                dataToUpdate.setValue(0, 1, value);
                zonechartToUpdate.draw(dataToUpdate, particularOptions);
                console.log(" Change in " + zone + " value " + value);
            }
        }
    };

}

function init() {
    google.load("visualization", "1", {packages:["corechart"]});
    data = new google.visualization.DataTable();
    data.addColumn('string', 'global');
    data.addColumn('number', 'Consumption');
    zoneDataMap = new Map();
    google.setOnLoadCallback(drawChart);

    function drawChart() {

        var t = $.get("/electricity/current",function(response) {
            data.addRow([response["date"],parseInt(response["value"])]);
        });

        var chart = new google.visualization.SteppedAreaChart(document.getElementById('global_chart'));

        chart.draw(data, options);
    }

    registerWebSocket();

}
