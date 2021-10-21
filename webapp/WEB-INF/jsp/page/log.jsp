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
	<link rel="stylesheet" href="/static/css/log/log.css"><!-- log CSS -->
<%--	<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--	<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
	<title>AVSB - 로그 페이지</title>
	</head>
<body>

	<div id='body-wrapper'>
	
		<%@ include file="../common/top.jsp" %>
	
		<div id='content-wrapper'>
		
			<%@ include file="../common/menu.jsp" %>
			
			<div id='right-content-wrapper'>
				<div id='right-content'>
					<!-- log content -->
					<div id="log-wrapper">
						<div id="selector-wrapper">
							<select id="servers" class="form-control" onchange="logChange()">
								<!-- <option>192.168.0.10</option>
								<option>192.168.0.100</option>
								<option>192.168.0.200</option> -->
							</select>
						</div>
						<div id="table-header-wrapper">
<%--							<table id="log-table" class="cell-border hover stripe" style="width: 100%">--%>
<%--								<thead>--%>
<%--								<tr>--%>
<%--									<th></th>--%>
<%--									<th>날짜</th>--%>
<%--									<th>아이피</th>--%>
<%--									<th>경로</th>--%>
<%--									<th>결과</th>--%>
<%--								</tr>--%>
<%--								</thead>--%>
<%--							</table>--%>
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
	<script src="/static/js/log/log.js"></script>
</body>
</html>