<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../forms/pswdForm.jsp" %>
<!-- top header -->  
<div id='top-wrapper'>
	<div id='company-logo-wraper'>
		<div id='company-logo'>Tera Information <strong style="color: #0878ce;">Security</strong></div>
	</div>
	<div id='product-logo-wraper'>
		<div id='product-logo'><span>Anti-Virus Signature Bridge</span></div>
		<div id='user-wrapper'>
			<div id='btn-user'>
				<span>admin 님 반갑습니다(관리자)</span>
				<img id='user-i' alt="" src="/static/img/dashboard/user.png" style="cursor: pointer;" width="32px;" height="32px;">
				<img id='arrow-down' alt="" src="/static/img/dashboard/arrow_down.png" width="12px;" height="12px;" style="left: -12px; position: relative;">
				<div id='btn-user-section' class='user_section'></div>
				<div id='drop-down'>
					<div class='menu-hover' style="cursor: pointer;border: 1px solid #cecece;" id='password-reset' ><span class='drop-menu' data-toggle="modal" data-target="#resetModal">Password Reset</span></div>
					<div class='menu-hover' style="cursor: pointer;border: 1px solid #cecece;" id='log-out'><span class='drop-menu'><a href="/avsb/logout">Log Out</a></span></div>
				</div>
			</div>
		</div>
	</div>
</div>