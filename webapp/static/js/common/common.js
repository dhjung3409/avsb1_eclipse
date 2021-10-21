function dashBoardClick(){
	location.replace("/dashboard/view")
}
function logViewerClick(){
	location.replace("/log/view")
}
function fileScanClick(){
	location.replace("/filescan/view")
}
function configClick(){
	location.replace("/config/view")
}
function accountClick(){
	location.replace("/account/view")
}
function systemInfoClick(){
	location.replace("/system/view")
}

function commmonInit(){
	// console.log("this is common javascript. ")
	toggleMenu();
	passwordChange()
}

function toggleMenu(){

	$("#btn-user-section ").on("click", function(){
		// console.log("THIS IS ARROW")
		var status = document.getElementById("drop-down").style.display;

		if(status == "none"){
			// console.log("THIS IS ARROW : show")
			document.getElementById("drop-down").style.display = "inline-block";
		}else{
			// console.log("THIS IS ARROW : none")
			document.getElementById("drop-down").style.display = "none";
		}

	});

}

function keyDisabled(){
	$(window).keydown(function(e){
		// console.log("keyCode",e.keyCode)
			if(e.keyCode==27){
				$("#resetModal").modal("show");
			}
	});
	$(window).keyup(function(e){
		if(e.keyCode==27){
			$("#resetModal").modal("show");
		}
	});
	$(window).keypress(function(e){
		if(e.keyCode==27){
			$("#resetModal").modal("show");
		}
	});


}

function passwordChange(){
	$("#password-reset-button").on("click",function(){
		var nowPass = document.getElementById("nowPass").value;
		var newPass = document.getElementById("newPass").value;
		var checkPass = document.getElementById("checkNewPass").value;

		if(newPass===checkPass){
			$.ajax({
				url:"/account/password/reset",
				method:"POST",
				data:{
					nowPass: nowPass,
					newPass: newPass
				},
				error: function(err_res){

				},
				success: function(suc_res){
					// console.log(suc_res)
					if(suc_res){
						var count =4;
						document.getElementById('btn-exit').style.display = "none";
						document.getElementById('password-reset-button').style.display = "none";
						document.getElementById('btn-exit-kr').style.display = "none";
						keyDisabled();
						countdown(count);

					}else{
						failNotif();
					}
				}
			})
		}else{
			failNotif();
		}
	})
}

function countdown(count){



	count--;
	var body = document.getElementById("reset-body");
	// console.log("body",body.childNodes);
	while(body.firstChild!=null) {
		body.removeChild(body.firstChild);
	}
	var htag = document.createElement("center");
	htag.setAttribute("style","font-size:20px;")
	var tagText = document.createTextNode(count+"초 뒤 자동 로그아웃됩니다.")
	htag.appendChild(tagText);
	body.appendChild(htag);
	if(count>0){
		setTimeout(function(){
			countdown(count)
		},1000)
	}else{
		$("#resetModal").modal("hide");
		document.location.href="/avsb/logout";
	}
}

function failNotif(){
	notif({
		type: "info",
		msg: "잘못 입력된 비밀번호 입니다.",
		position: "center",
		timeout: 1000,
		bgcolor: "#a11232",
		fade: true,
	});
}

commmonInit();