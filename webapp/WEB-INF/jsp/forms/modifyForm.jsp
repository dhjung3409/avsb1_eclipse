<%--
  Created by IntelliJ IDEA.
  User: jong
  Date: 2021/07/22
  Time: 11:11 오후
  To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!-- Modal -->
<button id="btn-mod-modal" type="button" class="account-btn" style="display:none;"
        data-toggle="modal" data-target="#modifyModal">계정 수정</button>

<div class="modal fade" id="modifyModal" tabindex="-1" role="dialog" aria-labelledby="modifyModalLabel" data-backdrop="static">
    <div class="modal-dialog" role="document">
        <div class="modal-content">

                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="modifyModalLabel">계정 수정</h4>
                </div>
                <div class="modal-body" id="mod-modal-body" name="false">

                    <div class="form-group mod-pass" style="display: none">
                        <label for="userModifyPw">패스워드</label>
                        <input type="password" class="form-control" id="userModifyPw" name="userModifyPw" autocomplete="off" maxlength="40">
                    </div>

                    <div class="form-group mod-pass" style="display: none">
                        <label for="userModifyPwC">패스워드 확인</label>
                        <input type="password" class="form-control" id="userModifyPwC" autocomplete="off" maxlength="40">
                    </div>

                    <div class="form-group">
                        <label for="userModifyRole">역할</label>
                        <select class="form-control" id="userModifyRole" name="userModifyRole">
                            <option value="admin">관리자</option>
                            <option value="user">유저</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="userModifyName">이름</label>
                        <input type="text" class="form-control" id="userModifyName" name="userModifyName" autocomplete="off" maxlength="20">
                    </div>

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" onclick="changePass()">비밀번호 변경</button>
                    <button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
                    <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="editUser();">수정</button>
                </div>
<%--            </form>--%>
        </div>
    </div>
</div>
