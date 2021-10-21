<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
	<%@ include file="../common/css.jsp" %>	<!-- common css -->
	<link rel="stylesheet" href="/static/css/system/system.css"><!-- system CSS -->
<%--	<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--	<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
	<title>AVSB - 시스템 페이지</title>
	</head>
<body>

	<div id='body-wrapper'>
	
		<%@ include file="../common/top.jsp" %>
	
		<div id='content-wrapper'>
		
			<%@ include file="../common/menu.jsp" %>
			
			<div id='right-content-wrapper'>
				<div id='right-content'>
					<!-- system content -->
					<div id="systemInfo-wrapper">

						<div id="serverInfo">
							<div class="sub-title">
								<label>서버 정보</label>
							</div>
							<div class="table-wrapper">
								<table class="table table-bordered">
									<colgroup>
										<col width="20%">
										<col width="80%">
									</colgroup>

									<!-- <tbody>
									<tr>
										<td style="text-align: center;">운영체제</td>
										<td style="padding-left: 40px;">System.getProperty("os.name")</td>
									</tr>
									<tr>
										<td style="text-align: center;">아키텍쳐</td>
										<td style="padding-left: 40px;">System.getProperty("os.arch")</td>
									</tr>
									<tr>
										<td style="text-align: center;">버전</td>
										<td style="padding-left: 40px;">System.getProperty("os.version")</td>
									</tr>
									</tbody> -->
								</table>
							</div>
						</div>

						<div id="node-list">
							<div class="sub-title">
								<label>연동 서버 리스트</label>
							</div>
							<div class="table-wrapper">
								<form enctype="multipart/form-data">
									<table class="table table-bordered">
										<colgroup>
											<col width="20%">
											<col width="80%">
										</colgroup>

										<!-- <tbody>
										<tr>
											<td style="text-align: center;">서버 아이피</td>
											<td style="padding-left: 40px;">현황</td>
										</tr>
										<tr>
											<td style="text-align: center;">192.168.0.1</td>
											<td style="padding-left: 40px;">연결됨</td>
										</tr>
										<tr>
											<td style="text-align: center;">192.168.0.2</td>
											<td style="padding-left: 40px;">연결안됨</td>
										</tr>

										</tbody> -->
									</table>
								</form>
							</div>
						</div>

						<div id="avsbInfo">
							<div class="sub-title">
								<label>AV Signature Bridge</label>
							</div>
							<div class="table-wrapper">
								<table class="table table-bordered">
									<colgroup>
										<col width="20%">
										<col width="80%">
									</colgroup>

									<!-- <tbody>
									<tr>
										<td style="text-align: center;">버전</td>
										<td style="padding-left: 40px;">1.0</td>
									</tr>
									<tr>
										<td style="text-align: center;">라이선스 기간</td>
										<td style="padding-left: 40px;">db에서 가져올것.</td>
									</tr>
									</tbody> -->
								</table>
							</div>
						</div>

						<div id="sdkInfo">
							<div class="sub-title">
								<label>엔진 정보</label>
							</div>
							<div class="table-wrapper">
								<table class="table table-bordered">
									<colgroup>
										<col width="20%">
										<col width="80%">
									</colgroup>
									<tbody>
									<tr>
										<td style="text-align: center;">엔진명</td>
										<td style="text-align: center;">버전</td>
									</tr>


									<!-- <tr>
										<td style="text-align: center;">TSEngine</td>
										<td style="text-align: center;">API를 통해 가져올것</td>
									</tr> -->


									</tbody>
								</table>
							</div>
						</div>

					</div>
				</div>
			</div>
		
		</div>
	</div>
	<%@ include file="../common/script.jsp" %>	<!-- common java script -->
	<script src="/static/js/system/system.js"></script>
</body>
</html>