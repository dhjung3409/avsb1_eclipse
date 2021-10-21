function getServerList(){
	$.ajax({
		url:"/etc/subip",
		method:"GET",
		data:{},
		error: function(err_res){
			// console.log("report config error")
		},
		success: function(suc_res){
			// console.log("report config success!")
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
		// listSpan.setAttribute("class","update-modal")
		// listSpan.setAttribute("data-toggle","modal")
		// listSpan.setAttribute("data-target","#updateModal")
		listSpan.appendChild(subIp);
		listSecondTd.appendChild(listSpan);
		
		listTr.appendChild(listFirstTd);
		listTr.appendChild(listSecondTd);
		
		listBody.appendChild(listTr);
	}

	// clickIP();
}
// function deleteProgressBar(className){
// 	var uploadLog = document.getElementById("upload-log")
// 	if(className!=null){
// 		for(var i=0;i<uploadLog.children.length;i++){
// 			if(className==uploadLog.children[i].className){
// 				uploadLog.removeChild(uploadLog.children[i]);
// 			}
// 		}
// 	}else{
// 		while(uploadLog.lastChild){
// 			uploadLog.removeChild(uploadLog.lastChild);
// 		}
// 	}
	// }
// }
// // var url = "";
// function clickIP(){
// 	$(".update-modal").on("click",function(){
// 		// console.log(this.innerText)
// 		// console.log(location.port)
// 		// console.log(protocol)
// 		var modalIp;
// 		var protocol = location.protocol.indexOf("http:")>=0?"http://":"https://";
// 		console.log(location.hostname)
// 		if(this.innerText==="127.0.0.1"){
// 			modalIp=location.hostname;
// 		}else{
// 			modalIp=this.innerText;
// 		}
// 		var url = protocol+modalIp+":"+location.port
//
// 		document.getElementById('protocol-url').value=url;
//
// 		updateRadio();
// 		deleteProgressBar();
// 		deleteTextArea();
// 		// uploadFile();
// 	})
// }
//
// function updateRadio(status){
// 	// console.log("now IP",modalIp)
// 	// var inputData={"ip":modalIp,"option":"radio"};
// 	var url = document.getElementById('protocol-url').value;
// 	$.ajax({
// 		url:url+ "/config/rest/update/radio",
// 		method:"POST",
// 		// data:{ip:modalIp,option:"radio"},
// 		beforeSend:function(br_res){
// 				// br_res.setRequestHeader("Access-Control-Allow-Origin",protocol+modalIp+":"+location.port)
// 		},
// 		error: function (err_res){
// 			console.log(err_res)
// 			alert("연결되지 않은 IP 입니다.");
// 			var table = document.getElementById("file-table");
// 			if(table.lastChild!=null){
// 				table.removeChild(table.lastChild);
// 			}
// 			$('#updateModal').modal("hide");
// 		},
// 		success: function(suc_res){
// 			console.log(suc_res)
// 			selectTable(suc_res,status);
// 		}
// 	})
// }
//
// function selectTable(data,status){
// 	var table = document.getElementById("file-table");
// 	if(table.lastChild!=null){
// 		table.removeChild(table.lastChild);
// 	}
// 	var tbody = document.createElement("tbody");
// 	for(var i=0;i<data["fileName"].length;i++){
// 		var tr=document.createElement("tr");
// 		var tdRadio = document.createElement("td");
// 		var tdDate = document.createElement("td");
// 		var tdName = document.createElement("td");
// 		var tdSize = document.createElement("td");
// 		var radio = document.createElement("input")
// 		radio.setAttribute("type","radio")
// 		radio.setAttribute("class", "radio-box")
// 		radio.setAttribute("name","update_check");
// 		radio.setAttribute("value",i);
// 		if(i==0&&status==="refresh"){
// 			radio.setAttribute("checked","true");
// 		}
// 		var dateText = document.createTextNode(data["fileDate"][i]);
// 		var nameText = document.createTextNode(data["fileName"][i]);
// 		var sizeText;
// 		if(data["fileSize"][i]>(1024*1024)){
// 			sizeText = document.createTextNode((data["fileSize"][i]/(1024*1024)).toFixed(2)+"MB");
// 		}else if(data["fileSize"][i]>1024){
// 			sizeText = document.createTextNode((data["fileSize"][i]/1024).toFixed(2)+"KB");
// 		}else{
// 			sizeText = document.createTextNode(data["fileSize"][i]+"Byte");
// 		}
// 		tdRadio.appendChild(radio)
// 		tdDate.appendChild(dateText);
// 		tdName.appendChild(nameText);
// 		tdSize.appendChild(sizeText);
// 		tr.appendChild(tdRadio);
// 		tr.appendChild(tdDate);
// 		tr.appendChild(tdName);
// 		tr.appendChild(tdSize);
// 		tbody.appendChild(tr);
// 	}
// 	table.appendChild(tbody);
// }
//
// function uploadFile(){
// 	// $("#upload-button").on("click",function(){
//
// 		deleteProgressBar();
// 		console.log("file size",$(".update-file")[0].files[0].size)
// 		var file = $(".update-file")[0].files[0];
// 		// console.log("file",file.target.form)
// 		var formData = new FormData($(".update-file")[0].form);
// 		formData.append("file",$(".update-file")[0].files[0]);
// 		var modalBody = document.getElementById("upload-log");
// 		console.log("modalBody",modalBody)
// 		var progDiv = document.createElement("div");
// 		progDiv.setAttribute("class","progress");
// 		var progress = document.createElement("div");
// 		progress.setAttribute("class","progress-bar");
// 		// progress.setAttribute("style","background-color:red;height:20px")
// 		progDiv.appendChild(progress);
// 		modalBody.appendChild(progDiv);
// 		// var inputData = ;
// 		var url = document.getElementById('protocol-url').value;
// 		$.ajax({
// 			url:url+"/config/rest/update/info",
//             method:"POST",
//
// 			data:{fileName:file.name, fileSize:file.size},
// 			error: function(){
// 				// $("#update-start").prop("disabled",false);
// 			},
// 			success: function(suc_res){
// 				console.log(suc_res)
// 				if(suc_res) {
// 					uploadFileAjax(formData);
// 				}else{
// 					// $("#update-start").prop("disabled",false);
// 					// alert("업데이트 파일이 아닙니다.");
// 					// console.log("button",document.getElementById("update-start"))
// 					// enableButton();
// 					alert("업데이트 파일이 아닙니다.")
//
//
// 				}
// 			}
// 		})
//
// 	// })
// }
// function enableButton(){
// 	$("#update-start").prop("disabled",false);
// }
//
// function uploadFileAjax(formData){
// 	var url = document.getElementById('protocol-url').value;
// 	$("#upload-button").prop("disabled",true);
// 	$.ajax({
// 		xhr: function() {
// 			// console.log("xhr")
// 			var xhr = new window.XMLHttpRequest();
// 			xhr.upload.addEventListener("progress", function(evt) {
//
// 				if (evt.lengthComputable) {
// 					var percentComplete = parseInt((evt.loaded / evt.total) * 100);
//
// 					console.log("total size",evt.total)
// 					$(".progress-bar").width(percentComplete + '%');
// 					$(".progress-bar").html(percentComplete+'%');
//
// 					if(percentComplete===100&&evt.loaded!==evt.total){
// 						xhr.abort();
// 					}
// 				}else{
// 					// console.log("xhr else")
// 				}
//
//
// 			}, false);
// 			console.log("test")
// 			return xhr;
// 		},
// 		url:url+"/config/rest/update/files",
// 		method:"POST",
// 		data: formData,
// 		processData: false,
// 		contentType: false,
// 		cache: false,
// 		beforeSend: function(bf_res){
// 			$(".progress-bar").width('0%');
// 			console.log("bf_res",bf_res)
// 			$("#update-modal-close").on("click",function(){
// 				console.log(bf_res)
// 			})
// 		},
// 		error: function(err_res){
// 			// $('#uploadStatus').html('<p style="color:#EA4335;">File upload failed, please try again.</p>');
// 			console.log(err_res)
// 			$("#update-start").prop("disabled",false);
// 		},
// 		success: function(suc_res){
// 			console.log("suc_res",suc_res)
// 			uploadRes(suc_res);
// 			updateRadio("refresh");
// 		}
//
// 	})
//
// }
//
// function uploadRes(res){
// 	deleteProgressBar("res-div");
// 	var modalBody = document.getElementById("upload-log");
// 	var resDiv = document.createElement("div");
// 	resDiv.setAttribute("class","res-div");
// 	var resLabel = document.createElement("label");
// 	resLabel.setAttribute("class","res-label");
// 	var resText;
// 	if(res===1){
// 		resText=document.createTextNode("업로드가 완료되었습니다.");
// 	}else{
// 		resText=document.createTextNode("업로드가 실패했습니다.");
// 	}
// 	resLabel.appendChild(resText);
// 	resDiv.appendChild(resLabel);
// 	modalBody.appendChild(resDiv);
// 	$("#upload-button").prop("disabled",false);
// }
//
// function updateStart(){
// 	$("#update-start").prop("disabled",true);
// 	var radios = document.getElementsByClassName("radio-box")
// 	var check = radios[0].checked?radios[0].value:radios[1].checked?radios[1].value:'-1';
// 	console.log(parseInt(check))
// 	var url = document.getElementById('protocol-url').value;
// 	$.ajax({
// 		url:url+"/config/rest/update/real_time",
// 		method:"POST",
// 		data:{num:parseInt(check)},
// 		beforeSend: function(){
// 			var output = document.getElementById("text-output");
// 			deleteTextArea();
// 			var h4 = document.createElement("h4");
// 			h4.setAttribute("style","flex:auto;text-align:center;")
// 			h4.appendChild(document.createTextNode("업데이트 중..."));
// 			output.appendChild(h4);
// 		},
// 		error : function(err_res){
// 			console.log(err_res)
// 		},
// 		success : function(suc_res){
// 			console.log(suc_res)
// 			if(suc_res==-1){
// 				alert("업데이트 파일이 선택되지 않았습니다.");
// 				deleteTextArea();
// 			}else {
// 				updateLog(suc_res);
// 			}
// 		}
// 	})
// }
//
// function updateLog(data){
// 	var output = document.getElementById("text-output");
// 	deleteTextArea();
// 	var textarea = document.createElement("textarea");
// 	textarea.setAttribute("disabled",true);
// 	textarea.setAttribute("style","resize:none;");
// 	textarea.setAttribute("class","text-output-area")
// 	for(var i=0;i<data.length;i++){
// 		var text = document.createTextNode(updateLogNum(data[i])+"\n");
// 		textarea.appendChild(text);
// 	}
// 	output.appendChild(textarea);
// 	$("#update-start").prop("disabled",false);
// }
// function deleteTextArea(){
// 	var output = document.getElementById("text-output");
// 	if(output.lastChild!=null){
// 		output.removeChild(output.lastChild);
// 	}
// }
//
// function updateLogNum(num){
// 	var text;
// 	if(num==1){
// 		text="업데이트 파일 압축 해제";
// 	}else if(num==2){
// 		text="업데이트 옵션 설정";
// 	}else if(num==3){
// 		text="업데이트 완료";
// 	}else{
// 		text=num
// 	}
//
// 	return text;
// }

function configReport(){
	var count = document.getElementById("report-count").value;

	$.ajax({
		url:"/config/report",
		method:"POST",
		data:{count:count},
		error: function(err_res){
			// console.log("report config error")
		},
		success: function(suc_res){
			// console.log("report config success!")
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
	// console.log("this is itemArr : ",itemArr)
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
				// console.log("server list not change (failed)");
			},
			success: function (suc_res) {
				// console.log("server list change! ");
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
			// console.log("log rotate time is not changed!")
		},
		success: function (suc_res) {
			// console.log("log rotate time is changed!")
			notice('로그 설정이 변경 되었습니다.');
		}
	});
}

function addSubServer() {
	var server = document.getElementById('server').value;
	var ipReg = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;

	if (ipReg.test(server) == true) {
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