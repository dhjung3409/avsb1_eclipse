function hideLogList() {
	document.getElementById("log-table").style.visibility = "hidden";
}

function showLogList() {
	document.getElementById("log-table").style.visibility = "visible";
}

// {"servers":[127.0.0.1,192.168.0.1,192.168.0.2]}
// {}

function getLogIP(){
	$.ajax({
		url:"/etc/subip",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			
		},
		success:function(suc_res){
			selectOption(suc_res)
		}
	})
}

function selectOption(ip){
	var sop = document.getElementById("servers")	
	for(var i=0;i<ip.length;i++){
		var option = document.createElement("option");
		option.value=ip[i];
		option.text=ip[i];
		if(ip[i]=='127.0.0.1'){
			option.setAttribute('selected','selected');
			getLogList(ip[i]);
		}
//		sop.add(option,sop[i])		
		sop.appendChild(option)
	}
	
}

function getLogList(ip) {
//	hideLogList()
	
	$.ajax({
		url:"/log/list",
		method:"GET",
		data:{ip:ip},
		cache:false,
		error: function(err_res){
		},
		success: function (suc_res){
			logTable(suc_res);
		}
	});

}


function createDataTables(data){

	$('#log-table').DataTable( {
		processing: true,
		data: data,
		ordering: true,
		responsive: {
			details: {
				type: 'column',
				target: 0
			}
		},
		language: {
			emptyTable: "데이터가 없습니다.",
			lengthMenu: "페이지당 _MENU_ 개씩 보기",
			info: "현재 _START_ - _END_ / _TOTAL_건",
			infoEmpty: "데이터 없음",
			infoFiltered: "( _MAX_건의 데이터에서 필터링됨 )",
			search: "검색 : ",
			zeroRecords: "일치하는 데이터가 없습니다.",
			loadingRecords: "로딩중...",
			processing: "잠시만 기다려 주세요...",
			paginate: {
				first: "Begin",
				next: "Next",
				previous: "Prev",
				last: "End",
			},
		},
		columnDefs: [
			{
				targets: 0,
				checkboxes: {
					'selectRow': true
				},
				render: function ( data, type, row, meta ) {
					return '<input class="del-item dt-checkboxes" type="checkbox" id=item_number'+data+'>';
				}
			}
		],
		select: 'multi',
		order: [[1, 'desc']],
		column: [
			{data : 'no'},
			{data : 'date'},
			{data : 'client'},
			{data : 'path'},
			{data : 'result'}
		]

	} );
	
}


function logTable(data){

	var thw = document.getElementById("table-header-wrapper");
	thw.removeChild(thw.firstChild);

	var thList = ["","날짜","아이피","경로","결과"];
	var logth = [document.createElement("th"),document.createElement("th"),document.createElement("th"),document.createElement("th"),document.createElement("th")];
	var logtr = document.createElement("tr");
	var logthead = document.createElement("thead");
	var clt = document.createElement("table");
	
	clt.setAttribute("id","log-table")
	clt.setAttribute("class","cell-border hover stripe")
	clt.setAttribute("style","width: 100%")

	for(var i=0; i<5; i++){
		var text = document.createTextNode(thList[i])
		logth[i].appendChild(text);
		logtr.appendChild(logth[i])
		logthead.appendChild(logtr)
	}

	logthead.appendChild(logtr)
	clt.appendChild(logthead)
	var creclt = thw.appendChild(clt);
	
	if(creclt != null){
		createDataTables(data);
		var input = document.getElementsByTagName("input");
		for(var i=0;i<input.length;i++){
			if(input[i].getAttribute("type")==='search'){
				input[i].setAttribute("maxlength",100);
			}
		}
	}

}

function logChange(){
	var sop = document.getElementById("servers");
	var ip = sop.options[sop.selectedIndex].value;
	getLogList(ip+"")
}

function logInit(){
	var testip = "127.0.0.1";

	getLogIP();
}

logInit();
