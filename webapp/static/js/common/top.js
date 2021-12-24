function getServerTime(){
    var date;
    $.ajax({
        url: "/etc/clock/time",
        method:"GET",
        data:{},
        cache:false,
        async:false,
        error: function(err_res){

        },
        success: function(suc_res){
            date = new Date(suc_res);
        }
    })
    return date;
}
function getUserInfo(){
    var locate = window.location.href
    if(locate.indexOf("/login")==-1) {
        $.ajax({
            url: "/etc/license/user/check",
            method: "GET",
            data: {},
            error: function (err_res) {

            },
            success: function (suc_res) {
                setUserInfo(suc_res)
            }
        })
    }
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

function inputServerTime(serverTime){
    // console.log(serverTime.format("yyyy/MM/dd HH:mm"));

    var serverYear = serverTime.getFullYear();
    var serverMonth = serverTime.getMonth()+1;
    var serverDay = serverTime.getDate();
    var serverHour = serverTime.getHours();
    var serverMinute = serverTime.getMinutes();

    if(serverMonth<10){
        serverMonth="0"+serverMonth;
    }

    if(serverDay<10){
        serverDay="0"+serverDay;
    }

    if(serverHour<10){
        serverHour="0"+serverHour;
    }

    if(serverMinute<10){
        serverMinute="0"+serverMinute;
    }

    var serverFormat =  serverYear + "-" + serverMonth + "-" + serverDay + " " + serverHour + ":" + serverMinute;
    var serverTimeText=document.createTextNode(serverFormat);
    var serverTimeSpan = document.getElementById("server-time");

    while(serverTimeSpan.firstChild!=null){
        serverTimeSpan.removeChild(serverTimeSpan.firstChild);
    }

    serverTimeSpan.appendChild(serverTimeText);
}
function setServerTime(){
    var serverTime=getServerTime();
    inputServerTime(serverTime);
}

function topInit() {
    if (document.getElementById("empty-top") == null) {
        var serverDate = getServerTime();
        inputServerTime(serverDate);
        setTimeout(function () {
            setServerTime();
            setInterval(function () {
                setServerTime();
            }, 60 * 1000);
        }, (60 - serverDate.getSeconds()) * 1000)
    }

    getUserInfo();
}
topInit();