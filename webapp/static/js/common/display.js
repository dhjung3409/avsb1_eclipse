//reactive web display
function displayInit(){
    var window_w = $(window).width();
    var window_h = $(window).height();
    var rs_w = window_w-240;
    var rs_h = window_h-120;

    if(rs_w <= 960){
        $("#right-content-wrapper").css("width",960);
    }else{
        $("#right-content-wrapper").css("width",rs_w);
        $("#right-content-wrapper").css("height",rs_h);
    }

    var doc_w = $(document).width();
    if(doc_w <= 1200){
        $("#right-content-wrapper").css("width",960);
    }
}

function display(){

    $(window).resize(function(){

        var window_w = $(window).width();
        var window_h = $(window).height();
        var rs_w = window_w-240;
        var rs_h = window_h-120;

        if(rs_w <= 960){
            $("#right-content-wrapper").css("width",960);
        }else{
            $("#right-content-wrapper").css("width",rs_w);
            $("#right-content-wrapper").css("height",rs_h);
        }

        var doc_w = $(document).width();
        if(doc_w <= 1200){
            $("#right-content-wrapper").css("width",960);
        }

        var element =  document.getElementById('current-chart-wrapper');
        if (typeof(element) != 'undefined' && element != null) {
            var jboxHeight = document.getElementById("current-chart-wrapper").offsetHeight;
            document.getElementById("malware-chart-wrapper").style.height = jboxHeight+"px";
            document.getElementById("current-log-wrapper").style.height = jboxHeight+"px";
            document.getElementById("malchart").style.height = Number(jboxHeight)-Number(jboxHeight/7)+"px";
        }

    });

}

display();
displayInit();