function tableForm(data,obj,id){
	var serverInfo = document.getElementById(id).getElementsByClassName("table-wrapper");
	var table = serverInfo[0].getElementsByTagName("table");
	var infoTbody = document.createElement("tbody");
	if(typeof obj ==="object"){
		for(var variable in data){
			var serverTr = document.createElement("tr");
			
			var serverFirstTd = document.createElement("td");
			serverFirstTd.setAttribute("style","text-align: center;");
			var system = document.createTextNode(obj[variable]);		
			serverFirstTd.appendChild(system);
			
			var serverSecondTd = document.createElement("td");
			serverSecondTd.setAttribute("style","padding-left: 40px;");
			var info = document.createTextNode(data[variable]);		
			serverSecondTd.appendChild(info);
			
			serverTr.appendChild(serverFirstTd);
			serverTr.appendChild(serverSecondTd);
			
			infoTbody.appendChild(serverTr);
		}		
	}else if(typeof obj === "string"){
		for(var i=0;i<data.length;i++){
			
			var serverTr = document.createElement("tr");
			
			var serverFirstTd = document.createElement("td");
			serverFirstTd.setAttribute("style","text-align: center;");
			var system = document.createTextNode(data[i]["server"]);		
			serverFirstTd.appendChild(system);
			var serverSecondTd = document.createElement("td");
			serverSecondTd.setAttribute("style","padding-left: 40px;");
			var upload=null;
			if(data[i]["result"]===true){
				if(data[i]['engine']=='null[null]'){
					data[i]['engine']='엔진이 비활성화 되었습니다.';
				}else{

				}
				var info = document.createTextNode("연결됨 / "+data[i]["engine"]);
			}else{
				var info = document.createTextNode("연결안됨");
			}


			serverSecondTd.appendChild(info);
			if(upload!=null){
				serverSecondTd.appendChild(upload);
			}
			
			serverTr.appendChild(serverFirstTd);
			serverTr.appendChild(serverSecondTd);
			
			infoTbody.appendChild(serverTr);			
		}
	}else{

		var serverTr = document.createElement("tr");
			
		var serverFirstTd = document.createElement("td");
		serverFirstTd.setAttribute("style","text-align: center;");
		var system = document.createTextNode(data["EngineName"]);		
		serverFirstTd.appendChild(system);
		
		var serverSecondTd = document.createElement("td");
		serverSecondTd.setAttribute("style","text-align: center;");
		if(data["EngineVersion"]=="null[null]"){
			data["EngineVersion"]='엔진이 비활성화 되었습니다.';
		}
		var info = document.createTextNode(data["EngineVersion"]);
		serverSecondTd.appendChild(info);
		
		serverTr.appendChild(serverFirstTd);
		serverTr.appendChild(serverSecondTd);
		
		infoTbody.appendChild(serverTr);			
		
	}
	table[0].appendChild(infoTbody);
}
function serverInfo(data){
	var obj = {os:"운영체제",arch:"아키텍처",version:"버전"};
	var id = "serverInfo";
	tableForm(data,obj,id);
	
}

function serverList(data){
	var id = "node-list";
	var obj = "string";
	tableForm(data,obj,id);

	
}

function avsbInfo(data){
	var id = "avsbInfo";
	var obj = {version:"버전",License:"라이센스 기간"};
	tableForm(data,obj,id);
}

function engineInfo(data){
	var id = "sdkInfo";
	var obj = 0;
	tableForm(data,obj,id);
}

function getInfo(){
	$.ajax({
		url:"/system/rest/server/info",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			
		},
		success:function(suc_res){
			serverInfo(suc_res);
			
		}
	})
}
function getStatus(){
	$.ajax({
		url:"/system/rest/server/status",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			
		},
		success:function(suc_res){
			serverList(suc_res);
		}
	})
}
function getEngine(){
	$.ajax({
		url:"/system/rest/server/engine",
		method:"GET",
		data:{},
		cache:false,
		error: function(err_res){
			
		},
		success:function(suc_res){
			engineInfo(suc_res);
		}
	})
}
function getAvsb(){
	$.ajax({
		url:"/system/rest/server/avsb",
		method:"GET",
		data:{},
		error: function(err_res){
			
		},
		success:function(suc_res){
			avsbInfo(suc_res);
		}
	})
}
function systemInit(){
	getInfo();
	getStatus();
	getEngine();
	getAvsb();

}

systemInit();