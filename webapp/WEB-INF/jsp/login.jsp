<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>

	<%@ include file="./common/css.jsp" %>	<!-- common css -->
	<link rel="stylesheet" href="/static/css/login/login.css"><!-- login CSS -->
<%--	<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--	<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
	<title>AVSB - 로그인 페이지</title>
</head>
<body>

	<div id='empty-top'></div>
	<div id='form-wrapper'>
		<form id="login-form" name='loginForm' method='post'>
			<div id='left-wrapper'>
				<div id='title'>
					<span>TERA</span>
				</div>
				<div id='sub-title'>
					<span>Anti-Virus Signature Bridge</span>
				</div>
			</div>
			<div id='right-wrapper'>
				
				<div id='sub-txt1'>
					<span>로그인</span>
				</div>
				<div id='userId-wrapper'>
					<input type="text" class='input-field' name='userId' id='userId' placeholder="Username" maxlength="50" autocomplete="off">
				</div>
				<div id='userPw-wrapper'>
					<input type="password" class='input-field' name='userPw' id='userPw' placeholder="Password" maxlength="100"  autocomplete="off"
					onkeypress="if (event.keyCode == 13){loginProcess();}">
				</div>
				<div id='sub-txt2'>
					<a href="/">비밀번호 재설정</a>
				</div>

				<div id='btn-wrapper'>
					<input type="button" id='btnLogin' value='로그인'  onclick="loginProcess()">
				</div>
				
				<c:if test="${message == 'error'}">
					<div style="color:red;text-align: center;width: 100%;font-size: 12px;">
						아이디 또는 비밀번호가 일치하지 않습니다.
					</div>
				</c:if>
				
			</div>
		</form>
	</div>
	<div id='empty-bottom'></div>

	<%@ include file="./common/script.jsp" %>	<!-- common java script -->
	<script src="/static/js/login/login.js"></script>
</body>
</html>