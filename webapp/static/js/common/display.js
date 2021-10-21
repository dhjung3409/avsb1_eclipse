//reactive web display
function display(){

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

    });

}

display();