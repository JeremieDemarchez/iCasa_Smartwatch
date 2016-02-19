var nodes, edges, groups, stateGroups, groupsByEdge, network, columnInfo;

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
    var panelHeading =  $("<div>Entity : "+nodes.get(elementId).label +"</div>").attr('class',"panel-heading");

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
        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item scroll-txt");
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
        var stateGroupListItem =  $("<li>"+key+"</li>").attr('class',"list-group-item scroll-txt");
        var stateGroupListValue =  $("<span>"+val+"</span>").attr('class',"badge");
        stateGroupListValue.appendTo(stateGroupListItem);
        stateGroupListItem.appendTo(stateGroupList);
        stateGroupList.appendTo(panelBody);
    });

    /** Append all to panel**/
    panelBody.appendTo(div_collapse);
    div_collapse.appendTo(panelState);
}

function createModalDisplayPanel(){
    var displayModal = document.getElementById('DisplayModalBody');
    var table =  $("<table></table>").attr('class',"table table-responsive").attr('id',"DisplayModalTable");
    var tableHead =  $("<thead></thead>");
    var tableRHead =  $("<tr><th>Context Model Group</th><th>Show</th></tr>");
    tableRHead.appendTo(tableHead);
    tableHead.appendTo(table);
    var tableBody =  $("<tbody></tbody>");

    $.each(groups, function(element, index){//groups.forEach(function(element, index, array){
        var row = $("<tr></tr>");
        var group =  $("<td>"+element+"</td>").attr('class', "col-md-4");
        var status =  $("<td>"+""+"</td>").attr('class',"enabler btn col-md-2").attr('data-group', element).attr('data-index',index);
        updateButton(status, stateGroups[index]);

        group.appendTo(row);
        status.appendTo(row);
        row.appendTo(tableBody);
    });

    tableBody.appendTo(table);
    table.appendTo(displayModal);


}

function graphDraw(){
    // create a network
    var container = document.getElementById('ContextNetwork');

    // provide the data in the vis format
    var data = {
        nodes: nodes,
        edges: edges
    };
    console.log('Data: ', data);

    var options = {
        nodes: {
            shape: 'dot',
            font:{
                strokeWidth: 2,
                strokeColor:'#FFFFFF'
            },
            scaling: {
                min: 20,
                max: 35,
                label: {
                    min: 18,
                    max: 30,
                    drawThreshold: 12,
                    maxVisible: 30
                }
            }
        },
        edges:{
            smooth: {forceDirection: 'none'}
        },
        interaction:{
            hover: true
        },
        physics: {
            forceAtlas2Based: {
                centralGravity: 0.01,
                springLength: 150
            },
            minVelocity: 0.75,
            solver: 'forceAtlas2Based'
        }
    };

    network = new vis.Network(container, data, options);

    $.each(stateGroups, function(index, state){
        groupVisibility(index, !state);
    });

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

    $('#DisplayModalBody').on('click', '.enabler', function(e) {
       e.preventDefault();

       var group = $(this).attr('data-group');
       var index = $(this).attr('data-index');
       var state = $(this).attr('data-state');
       var button = this;

       updateButton(button, (state == 'false'));
       groupVisibility(index, (state == 'true'));
   });

    function groupVisibility(group, hide){
        var visOpt = {};
        var options = {};

        stateGroups[group] = !hide;
        console.log('stateGroups ', stateGroups);
        visOpt[group] = {hidden: hide};
        options['groups'] = visOpt;
        network.setOptions(options);

        nodes.add({id:'update'});
        nodes.remove({id:'update'});

        updateEdges();
    }

    function updateEdges(){
        console.log('node update');
        $.each(groupsByEdge,function(edgeId, groups){
            var hide = false;
            $.each(groups, function(index, group, array){
                if(!stateGroups[group]){
                    hide = true;
                }
            });
            edges.update({id:edgeId, hidden: hide});
        });
    }
}


function graphInit(time) {
    //   registerWebSocket();
    clearBox("ColumnInfo");
    clearBox("ContextNetwork");
    clearBox("DisplayModalBody");

    nodes = new vis.DataSet();
    edges = new vis.DataSet();

    groups = {};
    stateGroups = {};
    groupsByEdge = {};
    var groupIndexOfNodes = {};
    columnInfo = $("#ColumnInfo");

    getGroups();

    function getGroups(){
        var u = $.get("/context/groups",function(data) {

            var numberOfGroups = data["size"];
            console.log('numberOfGroups ', numberOfGroups);

            for(var i = 0; i<numberOfGroups; i++){
                var name = data["group"+i+"name"];
                var state = data["group"+i+"state"];
                groups[name] = i;
                stateGroups[i] = state;
            }
            createModalDisplayPanel();
            getEntities();
        });
    }

    function getEntities(){
        var t = $.get("/context/entities",function(data) {
            var numberOfEntities = data["size"];
            console.log('numberOfEntities ', numberOfEntities);

            for(var j = 0; j<numberOfEntities; j++){
              var nameId = data["entity"+j+"id"];
              var hash = data["entity"+j+"hash"];
              var grp = data["entity"+j+"group"];

              nodes.add({
                  id: hash,
                  label: nameId,
                  group: groups[grp]
              });
              groupIndexOfNodes[hash] = groups[grp];
            }
            getRelations();
        });
    }

    function getRelations(){
        var y = $.get("/context/relations",function(data) {
            var numberOfRelations = data["size"];
            console.log('numberOfRelations ', numberOfRelations);
            for(var i = 0; i<numberOfRelations; i++){
                var name = data["relation"+i+"name"];
                var nameId = data["relation"+i+"nameId"];
                var sourceId = data["relation"+i+"sourceId"];
                var targetId = data["relation"+i+"targetId"];

                if ((null != nodes.get(sourceId)) && (null != nodes.get(targetId))){
                    var edgeId = ""+nameId+""+sourceId+""+targetId;
                    edges.add({
                        id: edgeId,
                        from: sourceId,
                        to: targetId,
                        arrows:'to',
                        name: name,
                        font: {align: 'horizontal'}
                    });
                    groupsByEdge [edgeId] = [groupIndexOfNodes[sourceId], groupIndexOfNodes[targetId]];

                    console.log('groupsByEdge',groupsByEdge);
                }
            }

            var timer = window.setTimeout(graphDraw, time);
        });
    }
}



