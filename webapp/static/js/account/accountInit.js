
function btnFormModal(){
    $("#btn-user-add-modal").click();
}

function btnModifyModal(){

    var items = document.querySelectorAll(".del-item");
    var ckb = false;

    $.each(items, function(idx,sub){
        var tmpObj = sub;
        var tmpCkb = $("#"+sub.id).prop("checked");
        if(tmpCkb == true){
            ckb=true;
        }
    });

    if(ckb == false){
        notif({
            type: "info",
            msg: "계정을 선택하여 주시기 바랍니다.",
            position: "center",
            timeout: 1000,
            bgcolor: "#bb4f61",
            fade: true,
        });
    }else{
        var count = 0;
        $.each(items, function(idx,sub){
            count += sub.checked;
        });

        if(count == 1) {
            $("#btn-mod-modal").click();
        }else{
            notif({
                type: "info",
                msg: "수정할 계정을 하나만 선택하여 주시기 바랍니다.",
                position: "center",
                timeout: 1000,
                bgcolor: "#bb4f61",
                fade: true,
            });
        }
    }
}

function deleteUser() {

    var itemArr = getCheckItem();

    var itemCount = 0;
    itemCount = itemArr.length;

    if(itemCount < 1){
        notif({
            type: "info",
            msg: "삭제할 계정을 선택하여 주십시오.",
            position: "center",
            timeout: 1000,
            bgcolor: "#a11232",
            fade: true,
        });
        return;
    }else{
        var result = confirm("삭제하시겠습니까?");
        if(result == true){

            $.ajax({
                url: "/account/userdel",
                method:"POST",
                data:{"items":itemArr},
                error: function(err_res){

                },
                success: function(suc_res){
                    btnRefresh();
                }
            });
        }
    }
}

function btnRefresh(){
    location.reload();
}