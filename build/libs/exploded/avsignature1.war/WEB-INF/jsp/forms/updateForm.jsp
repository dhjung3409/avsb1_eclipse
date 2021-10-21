<%--
  Created by IntelliJ IDEA.
  User: jong
  Date: 2021/07/22
  Time: 11:11 오후
  To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<input type="hidden" id="protocol-url">

<button id="btn-user-add-modal" type="button" class="account-btn" style="display: none"
        data-toggle="modal" data-target="#userAddModal">계정 추가</button>

<!-- Modal -->
<div class="modal fade" id="updateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" data-backdrop="static">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <form id="updateForm" enctype="multipart/form-data">
                <div class="modal-header">
                    <button type="button" id="update-modal-close" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="myModalLabel">업데이트</h4>
                </div>
                <div class="modal-body" id="update-body">
                    <div class="upload-input">
                        <input type="file" style="display: inline-block" class="update-file">
                        <button type="button" style="float: right" id="upload-button" onclick="uploadFile()">업로드</button>
                    </div>
                    <div id="upload-log">

                    </div>
                </div>
                <div class="modal-body" id="update-info">
                    <div class="upload-table">
                        <table id="file-table" class="table table-striped">

                        </table>
                    </div>
                    <div class="upload-button">
                        <button type="button" id="update-start" onclick="updateStart()">업데이트 시작</button>
                    </div>
                    <div class="text-area" id="text-output">

                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
<%--                    <button type="button" class="btn btn-primary" onclick="addUser();">등록</button>--%>
                </div>
            </form>
        </div>
    </div>
</div>
