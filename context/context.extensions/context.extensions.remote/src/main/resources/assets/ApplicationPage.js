function getApplication(providerId){

    t = $.get("/context/applications/"+providerId,function(data) {
        $.each(data,function(key,value){
            console.log("For Each " + key + " value " + value);
        });
    });
}

function getListOfApplications(){

    clearBox("applicationsSection");
    console.log("Get List of Applications");
    t = $.get("/context/applications",function(data) {
        $.each(data,function(key,value){
            getApplication(key)
        });
    });
}