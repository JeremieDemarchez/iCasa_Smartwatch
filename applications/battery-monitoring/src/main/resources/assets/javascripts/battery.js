var maMap = new Map();


function addNewDevicePieChart(deviceId,percentage){
    var deviceElement = $("#devices-chart > #"+deviceId);
    if(deviceElement.length == 0){
        drawDeviceElement(deviceId);
    }

    updateDevicePanel(deviceId,percentage);
    drawPieDeviceChart(deviceId,percentage);

}

function drawDeviceElement(id){
    var deviceChartElement = $("#devices-chart");

    var columnDiv = $("<div></div>").attr('class',"col-md-4").attr("id",id);
    var panelDiv = $("<div></div>").attr('class',"panel");
    var panelHeadingDiv = $("<div></div>").attr('class',"panel-heading");
    var panelTitle = $("<h3></h3>").attr('class',"panel-title").text(id);
    var panelBodyDiv = $("<div></div>").attr('class',"panel-body");
    var chartDiv = $("<div></div>").attr('id',"chartist-"+id);

    columnDiv.append(panelDiv);
    panelDiv.append(panelHeadingDiv);
    panelHeadingDiv.append(panelTitle)
    panelDiv.append(panelBodyDiv);
    panelBodyDiv.append(chartDiv);
    deviceChartElement.append(columnDiv);
}

function updateDevicePanel(id,percentage){
    var deviceChartElements = $("#devices-chart > #"+id+" > .panel");
    var bodyText;
    if(percentage > 60){
        deviceChartElements.attr("class","panel panel-success");
        bodyText = "Battery percentage : " + percentage + " %";
    }else if(percentage > 20){
        deviceChartElements.attr("class","panel panel-warning");
        bodyText = "Battery percentage : " + percentage + " %";
    }else if (percentage > 0){
        deviceChartElements.attr("class","panel panel-danger");
        bodyText = "Battery percentage : " + percentage + " %";
    } else {
        deviceChartElements.attr("class","panel panel-info");
        bodyText = "No information available for the moment";
    }
    var panelFooter = $("#devices-chart > #"+id+" > .panel > .panel-footer");
    if (panelFooter.length == 0){
        panelFooter = $("<div></div>").attr('class',"panel-footer");
    }
    panelFooter.text(bodyText);
    deviceChartElements.append(panelFooter);
}

function drawPieDeviceChart(id,percentage){
    var first=0;
    var second=0;
    var third=0;
    var finalWhite = 0;

    var finalGrey = 100-percentage;
    if (percentage > 60){
        third = percentage;
    } else if (percentage > 20){
        second = percentage;
    }else if(percentage > 0 ) {
        first = percentage;
    } else {
        finalWhite =100;
        finalGrey = 0;
    }

    var chart = new Chartist.Pie("#chartist-"+id, {
        series: [first,second,third,finalGrey,finalWhite]
    }, {
        donut: true,
        donutWidth: 60,
        startAngle: 270,
        total: 200,
        showLabel: false
    });

    maMap.set(id,chart);
}

function registerWebSocket() {
    var ws = $.easyWebSocket("ws://" + window.location.host + "/battery/ws");
    ws.onmessage = function (event) {
        var message = $.parseJSON(event.data);
        var node = generateValidDomId(message.nodeId);
        var percentage = message.level;
        var departure = message.departure;
        if (departure){
            $("#devices-chart > #"+node).remove();
            maMap.delete(node);
        }else {

            if(maMap.has(node)){
                var chart = maMap.get(node);
                var first=0;
                var second=0;
                var third=0;
                var finalWhite = 0;

                var finalGrey = 100-percentage;
                if (percentage > 60){
                    third = percentage;
                } else if (percentage > 20){
                    second = percentage;
                }else if(percentage > 0 ) {
                    first = percentage;
                } else {
                    finalWhite =100;
                    finalGrey = 0;
                }

                chart.update({
                    series: [first,second,third,finalGrey,finalWhite]
                });

                updateDevicePanel(node,percentage);
            }else{
                addNewDevicePieChart(node,percentage);
            }
        }

    };

}

function generateValidDomId(id){
    var str = id.replace(/^[^a-z]+|[^\w:.-]+/gi, "");
    return str;
}

function init() {

    $.get('/batteries').done(function(data){
        $.each(data,function (key,value) {
            addNewDevicePieChart(generateValidDomId(key),value)
        });
    });

    registerWebSocket();

}