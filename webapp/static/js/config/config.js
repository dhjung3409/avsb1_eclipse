function getServerList(){
	$.ajax({
		url:"/config/server/list",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
		},
		success: function(suc_res){
			// console.log("suc_res",suc_res)
			serverList(suc_res);
		}
	})
}
function serverList(data){

	var listBody = document.getElementById("serverList");

	for(var i=0;i<data.length;i++){
		if(data[i][0]==="http://"){
			data[i][0]="HTTP";
		}else if(data[i][0]=="https://"){
			data[i][0]="HTTPS";
		}else{
			continue;
		}
		var listTr = document.createElement("tr");
		var listFirstTd = document.createElement("td");
		if(data[i][1]!="127.0.0.1") {
			var check = document.createElement("input");

			check.setAttribute("type", "checkbox");
			check.setAttribute("class", "server");
			check.setAttribute("items", data[i][1]);

			listFirstTd.setAttribute("class", "cb-center");
			listFirstTd.appendChild(check);
		}
		var listSecondTd = document.createElement("td");
		var listSpan = document.createElement("span");
		var subIp = document.createTextNode(data[i][1]+" / "+data[i][0]);
		listSpan.appendChild(subIp);
		listSecondTd.appendChild(listSpan);
		
		listTr.appendChild(listFirstTd);
		listTr.appendChild(listSecondTd);
		
		listBody.appendChild(listTr);
	}

}


function configReport(){
	var count = document.getElementById("report-count").value;

	$.ajax({
		url:"/config/report",
		method:"POST",
		data:{count:count},
		error: function(err_res){
		},
		success: function(suc_res){
			notice('리포트 설정이 변경 되었습니다.');

		}
	})
}

function checkAll(){
	document.querySelector("#check-all").addEventListener("change", function (e) {
		e.preventDefault();
		var serverList = document.querySelectorAll(".server");
		for (var i = 0; i < serverList.length; i++) {
			serverList[i].checked = this.checked;
		}
	});
}

function delServers(){
	var itemArr = null;

	var items = document.querySelectorAll(".server");
	itemArr = [];
	for(var i=0;i<items.length;i++){
		var validCheck = items[i].checked;
		if(validCheck == true){
			var num = (items[i].getAttribute('items'));
			itemArr.push(num);
		}
	}
	if(itemArr.indexOf("127.0.0.1")!=-1){
		notif({
			type: "info",
			msg: "IP: 127.0.0.1은 삭제 할 수 없습니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#072b38",
			fade: true,
		});
	}else {
		$.ajax({
			url: "/config/serverlist",
			method: "POST",
			data: {items: itemArr},
			error: function (err_res) {
			},
			success: function (suc_res) {
				location.reload();
			}
		})
	}
	

}

function notice(context){
	notif({
		type: "info",
		msg: context,
		position: "center",
		timeout: 3000,
		bgcolor: "#072b38",
		fade: true,
	});
}

function noticeAlert(context){
	notif({
		type: "info",
		msg: "<b>"+context+"</b>",
		position: "center",
		timeout: 3000,
		bgcolor: "#9d2020",
		fade: true,
	});
}

function rotateLogConfig(){
	var rotate = document.getElementById('records').value;
	$.ajax({
		url: "/config/log",
		method: "POST",
		data: {rotate: rotate},
		error: function (err_res) {
		},
		success: function (suc_res) {
			notice('로그 설정이 변경 되었습니다.');
		}
	});
}

function checkNumber(event) {
	if(event.key >= 0 && event.key <= 9) {
		console.log("return",true);
		return true;
	}else {
		console.log("return",false);
		return false;
	}
}

function changePort(){
	var port = (document.getElementById('server-port').value).trim();


	console.log(port)

	if(port>=6000 && port<49151){
		$.ajax({
			url:"/config/server/port/change",
			method: "POST",
			data:{
				port:port
			},
			error: function(){
				noticeAlert('로컬 포트가 변경에 실패했습니다.');
				return;
			},
			success: function(){
				notice('로컬 포트가 변경 되었습니다.');
			}
		})
	}else{
		noticeAlert('설정 할 수 있는 포트 범위 : 6000~49151');

	}
	document.getElementById('server-port').value = "";
	document.getElementById('server-port').focus();
}

function addSubServer() {
	var server = document.getElementById('server').value;
	var ipReg = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
	var HTTP = document.getElementById('http-selected').value;
	var HTTPCheck = HTTP=="http://"||HTTP=="https://"?true:false;

	if (ipReg.test(server) == true && HTTPCheck == true) {

		$.ajax({
			url: "/config/server",
			method: "POST",
			data: {
				server: server,
				httpStatus:HTTP
			},
			error: function (err_res) {
				// console.log("sub server is unregisted!")
			},
			success: function (suc_res) {
				// console.log("sub server is registed!")
				notice('서버가 추가 되었습니다.');
				document.getElementById('server').value = "";
				location.reload();
			}
		});
		
	}else if(HTTPCheck == false){
		notice("SSL 인증서 적용 여부에 대해서 확인 바랍니다.");
		document.getElementById('server').focus();
	}else{
		notice('올바른 아이피가 아닙니다. 다시 입력 바랍니다.');
		document.getElementById('server').focus();
	}

}

function changeMalwareChartRefreshTime(){
	var time = document.getElementById('malware').value;
	$.ajax({
		url: "/config/malchart/time",
		method: "POST",
		data: {time: time},
		error: function (err_res) {
			// console.log("malware chart time is not changed!")
		},
		success: function (suc_res) {
			// console.log("malware chart time is changed!")
			notice('감염현황 차트 리로드 시간이 변경되었습니다.');
		}
	});
}

function changeCurrentChartRefreshTime(){
	var time = document.getElementById('current').value;
	$.ajax({
		url: "/config/current/time",
		method: "POST",
		data: {time: time},
		error: function (err_res) {
			// console.log("current chart time is not changed!")
		},
		success: function (suc_res) {
			// console.log("current chart time is changed!")
			notice('실시간 차트 리로드 시간이 변경되었습니다.');
		}
	});
}

function changeCurrentLogRefreshTime(){
	var time = document.getElementById('current-log').value;
	$.ajax({
		url: "/config/current/log",
		method: "POST",
		data: {time: time},
		error: function (err_res) {
			// console.log("current chart time is not changed!")
		},
		success: function (suc_res) {
			// console.log("current chart time is changed!")
			notice('실시간 로그 리로드 시간이 변경되었습니다.');
		}
	});
}

function configInit(){
	// console.log("this is config page")
	getServerList();
}

configInit();