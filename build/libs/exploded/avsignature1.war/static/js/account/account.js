function btnRefresh(){
	location.reload();
}

function btnFormModal(){


	$("#btn-user-add-modal").click();


}

function btnModifyModal(){

		var items = document.querySelectorAll(".del-item");
		// console.log("items",items);
		var count = 0;
		$.each(items, function(idx,sub){
			// console.log(idx);
			// console.log(sub);
			count += sub.checked;
		});

		// console.log("count",count)

	if(count == 1) {
		$("#btn-mod-modal").click();
	}



}


function validUserInfo(userId,userPw,userPwC,userRole,userName,type,status){

	var result = true;
	var idReg = /^[A-za-z|0-9|]{7,20}$/g;
	var pwReg = /^(?=.*[a-zA-z])(?=.*[0-9])(?=.*[$`~!@$!%*#^?&\\(\\)\-_=+])(?!.*[^a-zA-z0-9$`~!@$!%*#^?&\\(\\)\-_=+]).{7,20}$/g;
	var nameReg = /^[가-힣|a-z|A-Z|0-9|]+$/;

	if(!type=="edit") {
		var idValid = idReg.test(userId);
	}
	if(type=="edit"&&userPw==null){

	}else {
		var pwValid = pwReg.test(userPw);
	}
	var nameValid = nameReg.test(userName);

	if(idValid == false){
		result = false;
		notif({
			type: "info",
			msg: "아이디는 7자 이상 영문,숫자만 입력가능합니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#a11232",
			fade: true,
		});
		return result;
	}

	if(pwValid == false&&!(type=="edit"&&status==false)){
		result = false;
		notif({
			type: "info",
			msg: "패스워드는 7자 이상 영문,숫자,특수문자를 포함하여야 합니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#a11232",
			fade: true,
		});
		return result;
	}

	if((userPw === userPwC) == false&&!(type=="edit"&&status==false)){
		result = false;
		notif({
			type: "info",
			msg: "패스워드가 일치하지 않습니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#a11232",
			fade: true,
		});
		return result;
	}

	if(nameValid == false){
		result = false;
		notif({
			type: "info",
			msg: "이름은 영문 숫자만 입력가능합니다.",
			position: "center",
			timeout: 1000,
			bgcolor: "#a11232",
			fade: true,
		});
		return result;
	}
	return result;
}

function changePass(){
	var pass = document.getElementsByClassName("mod-pass");

	for(var i of pass){
		// console.log(i)
		if(i.style.display=='none') {
			i.style.display = 'inline';
		}else{
			i.style.display = 'none';
		}
	}
}

function editUser(){
	var userCount =  getCheckItem();
	// console.log(userCount)
	if(userCount.length===1) {
		var status = document.getElementsByClassName("mod-pass")[0].style.display=='none'?false:true;
		// console.log(status)
		var userPw = document.getElementById("userModifyPw").value;
		var userPwC = document.getElementById("userModifyPwC").value;
		var userRole = document.getElementById("userModifyRole").value;
		var userName = document.getElementById("userModifyName").value;
		var result = validUserInfo(null, userPw, userPwC, userRole, userName,"edit",status);
		// console.log("pw",userPw)
		// console.log("pwC",userPwC)
		// console.log(userId)
		if (result == true) {
			// console.log(userCount);


			$.ajax({
				url:"/account/mod",
				method:"POST",
				data: {
					userCount:userCount[0],
					status:status,
					userPw:userPw,
					userName:userName,
					userRole:userRole
				},
				error: function(err_res){
				// 	console.log(err_res)
				},
				success: function(suc_res){
					// console.log(suc_res)
					btnRefresh();
				}
			})
		}
	}
}

function addUser() {
	var userId = document.getElementById('userId').value;
	var userPw = document.getElementById('userPw').value;
	var userPwC = document.getElementById('userPwC').value;
	var userRole = document.getElementById('userRole').value;
	var userName = document.getElementById('userName').value;
	var result = validUserInfo(userId,userPw,userPwC,userRole,userName);

	if(result == true){
		document.getElementById('userForm').submit();
	}

}
function getCheckItem(){
	var itemArr = null;

	var items = document.querySelectorAll(".del-item");

	itemArr = [];
	for(var i=0;i<items.length;i++){

		var validCheck = items[i].checked;

		if(validCheck == true){
			var num = (items[i].id).substring(11);
			itemArr.push(num);
		}

	}

	return itemArr;
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

function refreshList() {

}

function accountTable(data){

	$('#members-table').DataTable( {
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
			{data : 'ID'},
			{data : 'Role'},
			{data : 'Name'}
		]

	} );
}

function hideAccountList() {
	document.getElementById("members-table").style.visibility = "hidden";
}

function showAccountList() {
	document.getElementById("members-table").style.visibility = "visible";
}

function getAccountList() {
	hideAccountList();

	$.ajax({
		url:"/account/list",
		method:"GET",
		data:{},
		error: function(err_res){

		},
		success: function (suc_res){
			//account table
			// console.log("list: suc_res",suc_res)
			accountTable(suc_res);
			showAccountList();
		}
	});

}

function accountInit(){
	getAccountList();
	// showModal();
	// showModalss();
}

accountInit();