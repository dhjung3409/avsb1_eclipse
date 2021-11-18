function getCurrentLog(){
    var reload=0;
    $.ajax({
        url:"/dashboard/server/last/line",
        method:"GET",
        cache:false,
        async:false,
        error:function(res_err){

        },
        success:function(res_suc){

            reload=res_suc["reload"];
            createTable(res_suc["currentLog"]);

        }
    });
    return reload;
}

// var table_global;

function createTable(data){
    var table = document.getElementById("currentLog");
    table.removeChild(table.getElementsByTagName("tbody")[0]);
    var tbody = document.createElement("tbody");
    var keyword = ["IP","Time","Path","Result"];
    var keyTr=createTr(keyword,table);
    tbody.appendChild(keyTr)
    for(var i=0;i<data.length;i++){
        var time = data[i]["time"];
        var splitTime = time.split(" ");

        var fullPath = data[i]["path"];
        var splitPath =fullPath.split("/");
        var path;
        if(splitPath.length>2||splitPath[splitPath.length-1].length>15) {
            path = splitPath[splitPath.length - 2]+"/"+splitPath[splitPath.length - 1];
            if(path.length>15){
                path=".../..."+path.substr(path.length-15,path.length);
            }else {
                path = ".../" + path;
            }
        }else{
            path = fullPath;
        }

        keyword=[data[i]["ip"],splitTime[1],path,data[i]["result"]];
        keyTr = createTr(keyword,tbody,time,fullPath);
        tbody.appendChild(keyTr)
    }
    table.appendChild(tbody)



}

function createTr(keyword,tbody,time,fullPath){

    var keyTr = document.createElement("tr");
    for(var i=0;i<keyword.length;i++){
        var keyTd = document.createElement("td");
        var text = document.createTextNode(keyword[i]);
        if(time!=null&&i===1){
            keyTd.setAttribute("title",time);
        }else if(fullPath!=null&&i==2){
            keyTd.setAttribute("title",fullPath);
        }
        keyTd.appendChild(text);
        keyTr.appendChild(keyTd);
    }

    // table_global.appendChild(tbody);
    return keyTr;
}


function currentLogInit(){
    var interval = getCurrentLog();
    setInterval(function(){
        getCurrentLog();
    },interval*1000*60);
}

currentLogInit();