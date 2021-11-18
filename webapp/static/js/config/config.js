function getServerList(){
	$.ajax({
		url:"/etc/subip",
		method:"GET",
		data:{},
		error: function(err_res){
		},
		success: function(suc_res){
			serverList(suc_res)
		}
	})
}
function serverList(data){

	var listBody = document.getElementById("serverList");

	for(var i=0;i<data.length;i++){
		var listTr = document.createElement("tr");
		var listFirstTd = document.createElement("td");
		if(data[i]!="127.0.0.1") {
			var check = document.createElement("input");

			check.setAttribute("type", "checkbox");
			check.setAttribute("class", "server");
			check.setAttribute("items", data[i]);

			listFirstTd.setAttribute("class", "cb-center");
			listFirstTd.appendChild(check);
		}
		var listSecondTd = document.createElement("td");
		var listSpan = document.createElement("span");
		var subIp = document.createTextNode(data[i]);
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
		timeout: 1000,
		bgcolor: "#072b38",
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

function addSubServer() {
	var server = document.getElementById('server').value;
	var ipReg = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;
	var HTTP = document.getElementById('http-selected').value;
	var HTTPCheck = HTTP=="http://"||HTTP=="https://"?true:false;

	if (ipReg.test(server) == true && HTTPCheck == true) {

		$.ajax({
			url: "/config/server",
			method: "POST",
			data: {server: server},
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