function getUserInfo(){
    $.ajax({
        url:"/etc/license/user/check",
        method:"GET",
        data:{},
        error:function(err_res){

        },
        success:function(suc_res){
            setUserInfo(suc_res)
        }
    })
}
function setUserInfo(data) {
    var userName = document.getElementById("muserName");
    var nameText = document.createTextNode(data["userInfo"]["userName"]);
    var userRole = document.getElementById("muserRole");

    var role;

    if (data["userRole"][0]["authority"] === "Role_admin") {
        role = " 관리자 ";
    } else {
        role = " 유저 ";
    }

    var roleText = document.createTextNode(role);
    var licenseRemain = data["licenseStatus"];
    if(licenseRemain<0){

    }else{
        var license = document.createTextNode("[라이센스 만료 "+licenseRemain+"일 전]");
        document.getElementById("license").appendChild(license);
    }
    userName.appendChild(nameText);
    userRole.appendChild(roleText);
}

function topInit(){
    getUserInfo();
}
topInit();