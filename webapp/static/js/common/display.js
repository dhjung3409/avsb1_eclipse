//reactive web display
function displayInit(){
    var window_w = $(window).width();
    var window_h = $(window).height();

    var rs_top_chart_w = $("#canvas-wrapper").width();
    var rs_current_w = $("#current-log-wrapper").width();
    var rs_top_chart_h = $("#malware-chart-wrapper").height();
    var rs_jbox_h = $("#top-jbox").height();
    var rs_w = window_w-240;
    var rs_h = window_h-120;

    if(rs_w <= 1160){
        $("#malware-chart-wrapper").css("width",696);
    }else{
        $("#malware-chart-wrapper").css("width",Number(rs_top_chart_w)-Number(rs_current_w+2));
        $("#malware-chart-wrapper").css("height",rs_jbox_h);
        $("#current-log-wrapper").css("height",rs_jbox_h);
        $("#malchart").css("width",Number(rs_top_chart_w)-Number(rs_current_w+2));
        $("#malchart").css("height",rs_jbox_h);
        $("#curchart").css("width",Number(rs_top_chart_w));
        $("#curchart").css("height",rs_jbox_h);
    }
}

function display(){
    $(window).resize(function(){
        location.reload();
    });
}

display();
displayInit();