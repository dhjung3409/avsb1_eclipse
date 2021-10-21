<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
	<%@ include file="../common/css.jsp" %>	<!-- common css -->
	<link rel="stylesheet" href="/static/plugins/datatables/datatables.min.css"><!-- dataTables CSS -->
	<link rel="stylesheet" href="/static/plugins/datatables/Buttons-1.7.1/css/buttons.dataTables.min.css"><!-- dataTables CSS -->
	<link rel="stylesheet" href="/static/plugins/datatables/datatables.checkbox.min.css"><!-- dataTables CSS -->
	<link rel="stylesheet" href="/static/css/account/account.css"><!-- account CSS -->

	<title>AVSB - 계정관리 페이지</title>
	</head>
<body>

	<div id='body-wrapper'>
	
		<%@ include file="../common/top.jsp" %>
	
		<div id='content-wrapper'>
		
			<%@ include file="../common/menu.jsp" %>

			<%@ include file="../forms/accountForm.jsp" %>
			<%@ include file="../forms/modifyForm.jsp" %>

			<div id='right-content-wrapper'>
				<div id='right-content'>
					<!-- account content -->

					<div id="account-wrapper">
						<div id="button-wrapper">
							<div class="top-button" id="add-user-wrapper">
								<span>
									<button id="btn-modal" type="button" class="account-btn" onclick="btnFormModal()">계정 추가</button>
								</span>
							</div>
							<div class="top-button" id="modify-wrapper" >
								<span>
<%--									<button id="btn-modify-modal" type="button" class="account-btn"--%>
<%--											data-toggle="modal" data-target="#modifyModal">계정 수정</button>--%>

									<button type="button" class="account-btn" onclick="btnModifyModal()">계정 수정</button>


								</span>
							</div>
							<div class="top-button" id="add-del-wrapper" onclick="deleteUser()">
								<span>계정 삭제</span>
							</div>

							<div class="top-button" id="refresh" onclick="btnRefresh()">
								<span>새로고침</span>
							</div>
						</div>

						<div id="table-header-wrapper">
							<table id="members-table" class="cell-border hover stripe" style="width: 100%">
								<thead>
								<tr>
									<th></th>
									<th>ID</th>
									<th>역할</th>
									<th>이름</th>
								</tr>
								</thead>
							</table>
						</div>

					</div>
					
				</div>
			</div>
		
		</div>
	</div>
	<%@ include file="../common/script.jsp" %>	<!-- common java script -->
	<script src="/static/plugins/datatables/datatables.min.js"></script> <!--data tables -->
	<script src="/static/plugins/datatables/Buttons-1.7.1/js/dataTables.buttons.min.js"></script> <!--data tables -->
	<script src="/static/plugins/datatables/datatables.checkbox.min.js"></script> <!--data tables -->
	<script src="/static/js/account/account.js"></script>
</body>
</html>