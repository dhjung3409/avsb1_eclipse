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
<%--		<div id="server-time-view"></div>--%>
		<div id='user-wrapper'>
			<div id='btn-user'>
				<div class="dropdown pull-right" id="drop-down-menu">
					<span id="server-time-text"> ServerTime: </span><span id="server-time">xxxx/xx/xx xx:xx</span><span id="license"></span>
					<a id="dLabel" data-target="#" href="/dashboard" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
						<span class="txt-white"><span id="muserName"></span>님 반갑습니다 ( <span id="muserRole"></span> )</span><span class="caret"></span>
					</a>
					<ul class="dropdown-menu" aria-labelledby="dLabel">
						<div class='menu-hover' style="cursor: pointer;border: 1px solid #cecece;" id='password-reset' ><span class='drop-menu' data-toggle="modal" data-target="#resetModal">Password Reset</span></div>
						<div class='menu-hover' style="cursor: pointer;border: 1px solid #cecece;" id='log-out'><span class='drop-menu'><a href="/avsb/logout">Log Out</a></span></div>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>