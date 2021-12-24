function initSelectBox(){
	var date = new Date();
	var dateObj = new Date(date);
	dateObj.setMinutes(date.getMinutes()+2)
	var month = dateObj.getMonth()+1;
	var today = dateObj.getDate();
	var hour = dateObj.getHours();
	var minute = dateObj.getMinutes();
	var monthSelect = document.getElementById('schedule-month');
	var daySelect = document.getElementById('schedule-day');
	var timeSelect = document.getElementById('schedule-time');
	var minSelect = document.getElementById('schedule-min');
	var typeSelect = document.getElementById("schedule-type");
	var options = typeSelect.options;
	for(var i=0;i<options.length;i++){
		if(options[i].innerText==="한번"){
			options[i].setAttribute('selected','selected');
		}
	}

	for(var i=1;i<13;i++){
		monthSelect.appendChild(setRepeatSelectOption(month,i))
	}

	for(var i=1;i<32;i++){
		daySelect.appendChild(setRepeatSelectOption(today,i));
	}

	for(var i=0;i<24;i++){
		timeSelect.appendChild(setRepeatSelectOption(hour,i));
	}

	for(var i=0;i<60;i++){
		minSelect.appendChild(setRepeatSelectOption(minute,i));
	}

	$("#schedule-type").on("change", function(){
		var type = document.getElementById('schedule-type').value;
		if(type == 'once'){
			document.getElementById('schedule-month-wrapper').style.display = "block";
			document.getElementById('schedule-day-wrapper').style.display = "block";
			document.getElementById('schedule-repeat-wrapper').style.display = "none";
		}else{
			document.getElementById('schedule-month-wrapper').style.display = "none";
			document.getElementById('schedule-day-wrapper').style.display = "none";
			document.getElementById('schedule-repeat-wrapper').style.display = "block";
		}

	})

}
function getCheckItem(className){
	var checkbox = document.getElementsByClassName(className);
	var items = [];
	for(var i=0;i<checkbox.length;i++){
		if(checkbox[i].checked){
			var no = checkbox[i].getAttribute("no");
			items.push(no);
		}
	}
	return items;
}

function deleteScheduler(){
	var itemArr=getCheckItem('check-status-scheduler');
	var scheduleMainCheck = document.getElementById('schedule-check');
	$.ajax({
		url:"/filescan/scheduler/delete",
		type:"POST",
		data:{items:itemArr},
		error: function (err_res){
		},
		success: function (suc_res){
			if(scheduleMainCheck.checked){
				scheduleMainCheck.checked=false;
			}
			getScheduler();
		}
	})

}

function deleteReport(){
	var itemArr=getCheckItem('check-status-report');
	var reportMainCheck = document.getElementById('report-check');
	$.ajax({
		url:"/filescan/report/delete",
		type:"POST",
		data:{items:itemArr},
		error: function (err_res){
		},
		success: function (suc_res){
			if(reportMainCheck.checked){
				reportMainCheck.checked=false;
			}
			getReport();
		}
	})

}

function setRepeatSelectOption(time,i){
	var option = document.createElement('option');
	if(i<10){
		option.setAttribute('value', "0"+i + "");
		option.appendChild(document.createTextNode("0"+i + ""));
	}else {
		option.setAttribute('value', i + "");
		option.appendChild(document.createTextNode(i + ""));
	}
	if(i == Number(time)){
		option.setAttribute('selected', "selected"); //today select
	}
	return option;
}
function uncheckCheckbox(){
	var scheduleMainCheck = document.getElementById('schedule-check');
	var reportMainCheck = document.getElementById('report-check');
	scheduleMainCheck.checked = false;
	reportMainCheck.checked= false;
}
function sendFilePath(){
	uncheckCheckbox();
	var filePath = document.getElementById("filepath").value;
	// console.log(filePath)
	var type = document.getElementById("schedule-type").value;
	var date = new Date();
	var dateObj = new Date(date);
	var time = date.getTime();
	// console.log(time)
	var year =  dateObj.getFullYear();
	// console.log(year)
	var period;
	if(type==="once"){
		period=document.getElementById("schedule-month").value+"/"+document.getElementById("schedule-day").value;
	}else{
		period=document.getElementById("schedule-repeat").value;
	}
	var hour = document.getElementById("schedule-time").value;
	var min = document.getElementById("schedule-min").value;


	if(filePath == "/" || filePath == ""){
		notif({
			type: "info",
			msg: "루트(/) 경로를 제외한 절대경로로 입력 바랍니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#a11232",
			fade: true,
		});
	}else{
		$.ajax({
			url:"/filescan/scheduler",
			method:"POST",
			data:{
				path:filePath,
				type:type,
				year:year,
				period:period,
				hour:hour,
				min:min,
				time:time
			},
			error: function(err_res){
				// console.log("err",err_res)
			},
			success:function(suc_res){
				// console.log("suc",suc_res)
				// console.log(document.getElementById("filepath"));
				if(suc_res.result) {
					document.getElementById("filepath").value = "";
					getReport();
					getScheduler();
				}else{
					notif({
						type: "info",
						msg: getMessage(suc_res.message),
						position: "center",
						timeout: 1000,
						bgcolor: "#a11232",
						fade: true,
					});
				}
			}
		});
	}



}
function getMessage(message){

	var result = "";

	try{
		switch (message) {
			case 1:
				result = "등록이 불가능한 시간입니다.";
				break;
			case 2:
				result = "존재하지 않는 경로입니다.";
				break;
			case 3:
				result = "읽기 권한이 존재하지 않습니다.";
				break;
			case 4:
				result = "12개까지의 경로만 등록 할 수 있습니다.";
				break;
			case 5:
				result = "이미 등록된 스케줄입니다.";
				break;
			case 6:
				result = "상위 경로로 이미 등록된 스케줄입니다.";
				break;
			default:
				result = "경로 등록에 문제가 발생했습니다.";
				break;
		}

	}catch (e) {
		result= "네트워크 에러";
	}

	return result;

}
function refreshReport(){
	var reportMainCheck = document.getElementById('report-check');
	reportMainCheck.checked = false;
	getReport();
}

function refreshSchedule(){
	var scheduleMainCheck = document.getElementById('schedule-check');
	scheduleMainCheck.checked = false;
	getScheduler();
}

function getScheduler(){
	$.ajax({
		url:"/filescan/scheduler",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			// console.log("err",err_res)
		},
		success:function(suc_res){
			//console.log("suc",suc_res)
			viewScheduler(suc_res)
		}
	});
}

function getReport(){
	$.ajax({
		url:"/filescan/report",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			// console.log("err",err_res)
		},
		success:function(suc_res){
			// console.log("suc",suc_res)
			viewReport(suc_res)

		}
	});
}
function viewScheduler(data){
	viewBody(data,"scheduler-table");
}

function viewReport(data){
	viewBody(data,"report-table");
}
function viewBody(data,id){
	// console.log(data)
	var table = document.getElementById(id);
	table.removeChild(table.getElementsByTagName("tbody")[0]);
	var tbody = document.createElement("tbody");
	for(var i=0;i<data.length;i++){
		var reportNum = data[i]['no'];
		var tr = document.createElement("tr");
		var tdNo = document.createElement("td");
		var noCheck = document.createElement("input");
		noCheck.setAttribute("type","checkbox");
		if(id=="scheduler-table") {
			noCheck.setAttribute("class", "check-status-scheduler");
		}else{
			noCheck.setAttribute("class", "check-status-report");
		}
		noCheck.setAttribute("no",reportNum)
		tdNo.appendChild(noCheck);

		var tdDate = document.createElement("td");


		var tdPath = document.createElement("td");
		var fullPath = data[i]['path'];
		if(fullPath[fullPath.length-1]==="/"){
			fullPath=fullPath.slice(0,-1);
		}
		tdPath.setAttribute("title",fullPath);

		var splitPath = data[i]['path'].split("/");
		var path = splitPath[splitPath.length-1];
		if(path.length>35){
			path=".../..."+path.substr(path.length-35,path.length);
		}else {
			path = ".../" + path;
		}
		var pathText = document.createTextNode(path);
		tdPath.appendChild(pathText);



		if(id==="scheduler-table") {
			var dateText = document.createTextNode(data[i]['reservationDate']);
			tdDate.appendChild(dateText);
			var tdType = document.createElement("td");
			var scanType = data[i]['type']==='once'?'한번':'반복';
			var typeText = document.createTextNode(scanType);
			tdType.appendChild(typeText);
			var tdCycle = document.createElement("td");
			var cycleType
			if(data[i]['cycle']==="weekly"){
				cycleType='매주';
			}else if(data[i]['cycle']==="monthly"){
				cycleType='매월';
			}else if(data[i]['cycle']==="daily"){
				cycleType='매일';
			}else{
				cycleType="";
			}
			var cycleText = document.createTextNode(cycleType);
			tdCycle.appendChild(cycleText)
			var tdStatus = document.createElement("td");
			var statusText = document.createTextNode(data[i]['result']);
			tdStatus.appendChild(statusText);
			tr.appendChild(tdNo);
			tr.appendChild(tdDate);
			tr.appendChild(tdPath);
			tr.appendChild(tdType);
			tr.appendChild(tdCycle);
			tr.appendChild(tdStatus);
		}else{
			var dateText = document.createTextNode(data[i]['endDate']);
			tdDate.appendChild(dateText);
			var tdReport = document.createElement("td");
			tdReport.setAttribute("no",reportNum);
			tdReport.setAttribute("class","viewReport");
			tdReport.setAttribute("data-toggle","modal");
			tdReport.setAttribute("data-target","#reportModal");


			var reportText = document.createTextNode(data[i]['report']);
			tdReport.appendChild(reportText);
			tr.appendChild(tdNo);
			tr.appendChild(tdDate);
			tr.appendChild(tdPath);
			tr.appendChild(tdReport);
		}
		tbody.appendChild(tr);
	}
	table.appendChild(tbody);
	// console.log(tbody)

	// var viewReport = document.getElementById(id).getElementsByClassName("subrb-body")[0];
	// while(viewReport.firstChild) {
	// 	viewReport.removeChild(viewReport.firstChild);
	// }
	// viewReport.setAttribute("style","margin: 40px;")
	// // console.log(viewReport);
	// // console.log(data);
	// for(num in data){
	// 	console.log("data["+num+"]: "+data[num])
	//
	// 	var reportText = document.createElement("p");
	// 	var text = document.createTextNode(data[num]);
	// 	repo
	// 	rtText.appendChild(text);
	// 	viewReport.appendChild(reportText);
	// }

	showReport();
	onScheduleCheckbox();
	onReportCheckbox();
}



//reactive web display
function display_wrapper(){

	var total_wrapper = $('#filescan-wrapper').height();
	var wrapper_top = 150;
	var wrapper_bottom = total_wrapper - wrapper_top;

	document.getElementById('wrapper-bottom').style.height = wrapper_bottom+"px";
	$(window).resize(function(){
		var total_wrapper = $('#filescan-wrapper').height();
		var wrapper_top = 150;
		var wrapper_bottom = total_wrapper - wrapper_top;
		document.getElementById('wrapper-bottom').style.height = wrapper_bottom+"px";
	});

	$(window).scroll(function(){
		var total_wrapper = $('#filescan-wrapper').height();
		var wrapper_top = 110;
		var wrapper_bottom = total_wrapper - wrapper_top;
		document.getElementById('wrapper-bottom').style.height = wrapper_bottom+"px";
	});

}

display_wrapper();

function showReport(){

	$(".viewReport").on("click", function(){

		var no = this.getAttribute('no');
		//console.log("number : "+no)
		// console.log(document.getElementById('myModalLabel'))
		$.ajax({
			url:'/filescan/report/result',
			method:'GET',
			data:{
				no:no
			},
			cache:false,
			beforeSend: function(bfs_res){
				// console.log("wait..");
			}
			,
			error: function(err_res){
				// console.log("err",err_res)
				errorModal();
			},
			success:function(suc_res){
				// console.log("suc",suc_res)
				if((suc_res["log"]==null||suc_res["log"].length==0)&&suc_res["report"]==null){
					//console.log("suc_res is null",suc_res)
					errorModal();
				}else {
					viewModal(suc_res);
				}

			}
		});
	});

}
function viewModal(data){
	//console.log(data)
	var modalBody = document.getElementById('report-body');
	modalBody.removeChild(document.getElementById('report-print'));

	var reportPrint = document.createElement('div');
	reportPrint.setAttribute('class','form-group');
	reportPrint.setAttribute('id','report-print');
	// console.log(data["log"])
	if(data["report"]==null){
		// console.log("report null")
		errorText(reportPrint,"파일이 손상되거나 삭제되었습니다.");
	}else {
		// console.log("report not null")
		for (var value in data["report"]) {
			var ptag = document.createElement("p");
			var dataText = document.createTextNode(value + " : " + data["report"][value]);
			ptag.appendChild(dataText);
			reportPrint.appendChild(ptag);
		}
	}
	var hr = document.createElement("hr");
	hr.setAttribute("class","solid");
	reportPrint.appendChild(hr);
	var notNormal = data["report"]["total"]-data["report"]["normal"];
	if(notNormal==0&&data["log"].length==0){
		errorText(reportPrint,"이상이 감지되지 않았습니다.");
	}else if(data["log"]==null||data["log"].length==0){
		errorText(reportPrint,"파일이 손상되거나 삭제되었습니다.");
	}else {
		for (var i = 0; i < data["log"].length; i++) {
			for (var value in data["log"][i]) {
				var ptag = document.createElement("p");
				var dataText = document.createTextNode(value + " : " + data["log"][i][value]);
				ptag.appendChild(dataText);
				reportPrint.appendChild(ptag);
			}
			var brtag = document.createElement("br");
			reportPrint.appendChild(brtag);
		}
	}
	modalBody.appendChild(reportPrint);

}

function errorModal(){
	var modalBody = document.getElementById('report-body');
	modalBody.removeChild(document.getElementById('report-print'));

	var reportPrint = document.createElement('div');
	reportPrint.setAttribute('class','form-group');
	reportPrint.setAttribute('id','report-print');
	errorText(reportPrint,"파일이 손상되거나 삭제되었습니다.");
	modalBody.appendChild(reportPrint);
}

function errorText(reportPrint,message){
	var htag = document.createElement("h6");
	var dataText = document.createTextNode(message);
	htag.appendChild(dataText);
	reportPrint.appendChild(htag);
}

function onCheckboxes(id){
	var mainCheck = document.getElementById(id).checked;
	var checkbox
	if(id=="schedule-check") {
		checkbox = document.getElementsByClassName("check-status-scheduler");
	}else{
		checkbox = document.getElementsByClassName("check-status-report");
	}

	for(var i=0;i<checkbox.length;i++){
		checkbox[i].checked=mainCheck;
	}

}
function onScheduleCheckbox(){
	$('.check-status-scheduler').on("click", function(){
		checkboxModify("check-status-scheduler")
	});
}

function onReportCheckbox(){
	$('.check-status-report').on("click", function(){
		checkboxModify("check-status-report")
	});
}

function checkboxModify(className){
	var checkboxes = document.getElementsByClassName(className);
	var count = 0;
	for(var i=0;i<checkboxes.length;i++){
		if(checkboxes[i].checked){
			++count;
		}
	}
	var checkAll
	if(className=="check-status-scheduler"){
		checkAll = document.getElementById("schedule-check");
	}else{
		checkAll = document.getElementById("report-check");
	}
	if(count===checkboxes.length){
		checkAll.checked = true;
	}else{
		checkAll.checked = false;
	}
}

function filescanInit(){
	// console.log("this is filescan page")
	uncheckCheckbox();
	getScheduler();
	getReport()
	initSelectBox();

}

filescanInit();