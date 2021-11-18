
function btnDate(){
    // console.log("this is changed!")
}

function dashboardDisplayInit(){
    var jboxHeight = document.getElementById("current-chart-wrapper").offsetHeight;
    document.getElementById("malware-chart-wrapper").style.height = jboxHeight+"px";
    document.getElementById("current-log-wrapper").style.height = jboxHeight+"px";
}

function dashboardDisplay(){
    $(window).trigger('resize');

    window.onresize = function(e){
        //console.log("this is work?")
        var jboxWidth = document.getElementById("current-chart-wrapper").offsetWidth;
        var jboxHeight = document.getElementById("current-chart-wrapper").offsetHeight;
        document.getElementById("malware-chart-wrapper").style.height = jboxHeight+"px";
        document.getElementById("current-log-wrapper").style.height = jboxHeight+"px";
        document.getElementById("malchart").style.height = Number(jboxHeight)-Number(jboxHeight/7)+"px";
    }


}

function dashboardInit(){
	// console.log("this is dashboard page")
    dashboardDisplayInit();
    // dashboardDisplay();
}

dashboardInit();