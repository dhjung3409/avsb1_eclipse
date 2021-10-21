<%--
  Created by IntelliJ IDEA.
  User: jong
  Date: 2021/07/22
  Time: 11:11 오후
  To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<button id="btn-user-add-modal" type="button" class="account-btn" style="display: none"
        data-toggle="modal" data-target="#userAddModal">계정 추가</button>

<!-- Modal -->
<div class="modal fade" id="userAddModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form id="userForm" action="/account/useradd" method="post">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">계정 추가</h4>
                </div>
                <div class="modal-body">

                    <div class="form-group">
                        <label for="userId">아이디</label>
                        <input type="text" class="form-control" id="userId" name="userId" autocomplete="off" maxlength="20">
                    </div>

                    <div class="form-group">
                        <label for="userPw">패스워드</label>
                        <input type="password" class="form-control" id="userPw" name="userPw" autocomplete="off" maxlength="40">
                    </div>

                    <div class="form-group">
                        <label for="userPwC">패스워드 확인</label>
                        <input type="password" class="form-control" id="userPwC" autocomplete="off" maxlength="40">
                    </div>

                    <div class="form-group">
                        <label for="userRole">역할</label>
                        <select class="form-control" id="userRole" name="userRole">
                            <option value="admin">관리자</option>
                            <option value="user">유저</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="userName">이름</label>
                        <input type="text" class="form-control" id="userName" name="userName" autocomplete="off" maxlength="20">
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
                    <button type="button" class="btn btn-primary" onclick="addUser();">등록</button>
                </div>
            </form>
        </div>
    </div>
</div>
