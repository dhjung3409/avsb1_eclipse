
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<!-- Modal -->
<div class="modal fade" id="resetModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form id="resetForm" >
                <div class="modal-header">
                    <button id="btn-exit" type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="password-Change">패스워드 변경</h4>
                </div>
                <div class="modal-body" id="reset-body">

                    <div class="form-group">
                        <p>현재 비밀번호 입력</p> <input style="width: 100%;" type="password" id="nowPass"/><p/>
                        <p>새 비밀번호 입력</p> <input style="width: 100%;" type="password" id="newPass"/><p/>
                        <p>새 비밀번호 확인</p> <input style="width: 100%;" type="password" id="checkNewPass"/><p/>
                    </div>

<%--                    <div class="form-group" id="report-print">--%>
<%--                    </div>--%>

                </div>
                <div class="modal-footer">
                    <input type="button" class="btn btn-default" id="password-reset-button" value="확인"/>
                    <button type="button" class="btn btn-default" id="btn-exit-kr" data-dismiss="modal">닫기</button>
                </div>
            </form>
        </div>
    </div>
</div>
