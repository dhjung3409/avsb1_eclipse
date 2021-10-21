<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
<%@ include file="../common/css.jsp" %>    <!-- common css -->
<link rel="stylesheet" href="/static/css/filescan/filescan.css"><!-- filescan CSS -->
<%--<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
<title>AVSB - 파일검사 페이지</title>
</head>
<body>

<div id='body-wrapper'>

    <%@ include file="../common/top.jsp" %>

    <div id='content-wrapper'>

        <%@ include file="../common/menu.jsp" %>
        <%@ include file="../forms/reportForm.jsp" %>

        <div id='right-content-wrapper'>
            <div id='right-content'>
                <!-- filescan content -->
                <div id="filescan-wrapper">

<%--                    <div style="width: 100%;height: 200px; position: relative;">--%>
<%--                        <div class="row" style="height: 200px">--%>
<%--                            <div class="col-lg-6 col-md-12 col-sm-12 col-xs-12" style="background-color: #1b6d85;height: 200px"></div>--%>
<%--                            <div class="col-lg-6 col-md-12 col-sm-12 col-xs-12" style="background-color: yellow; height: 200px"></div>--%>
<%--                        </div>--%>
<%--                    </div>--%>

                    <div id="wrapper-top">
                        <div class="dummy-body"></div>
                        <div id="wrapper-top-body">
                            <label class="sub-title" for="btn-scan" id="file-label">파일 경로</label>
                            <input class="form-control" type="text" id="filepath" name="filepath" maxlength="500">
                            <input class="form-control" type="button" id="btn-scan" value="등록" onclick="sendFilePath()">

<%--                            <button class="form-control" id="btn-modal" type="button"--%>
<%--                                    data-toggle="modal" data-target="#reportModal" style="width: 100px;position: relative;left: 20px;"--%>
<%--                            >모달테스트</button>--%>
                        </div>
                        <div id="wrapper-middle-body">
                            <label class="schedule-title">유형</label>
                            <select id="schedule-type" class="form-control">
                                <option value="once">한번</option>
                                <option value="repeat">반복</option>
                            </select>

                            <div id="schedule-repeat-wrapper" style="display: none">
                                <select id="schedule-repeat" class="form-control">
                                    <option value="daily">매일</option>
                                    <option value="weekly">매주</option>
                                    <option value="monthly">매달</option>
                                </select>
                            </div>

                            <div id="schedule-month-wrapper">
                                <select id="schedule-month" class="form-control"></select>
                                <label class="schedule-title">월</label>
                            </div>

                            <div id="schedule-day-wrapper">
                                <select id="schedule-day" class="form-control"></select>
                                <label class="schedule-title">일</label>
                            </div>

                            <div id="schedule-time-wrapper">
                                <select id="schedule-time" class="form-control"></select>
                                <label class="schedule-title">시</label>
                            </div>

                            <div id="schedule-min-wrapper">
                                <select id="schedule-min" class="form-control"></select>
                                <label class="schedule-title">분</label>
                            </div>
                        </div>

                    </div>

                    <div id="wrapper-bottom" >
                        <div class="contents" id="scheduler-wrapper">
                            <div class="subrb-title">
                                <label>스케줄러 현황</label>
                                <input type="button" id="btn-remove-scheduler" class="btn btn-jbtn" value="삭제" onclick="deleteScheduler()">
                                <input type="button" id="btn-refresh-scheduler" class="btn btn-jbtn" value="새로고침" onclick="refreshSchedule()">
                            </div>
                            <div class="subrb-body">
                                <table id="scheduler-table" class="table table-striped" style="width: 100%;margin-top: 10px;">
                                    <thead>
                                    <tr>
                                        <th><input type="checkbox" id="schedule-check" onclick="onCheckboxes(this.id)"/></th>
                                        <th>날짜</th>
                                        <th>경로</th>
                                        <th>타입</th>
                                        <th>주기</th>
                                        <th>상태</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="contents" id="report-wrapper">
                            <div class="subrb-title">
                                <label>리포트 현황</label>
                                <input type="button" id="btn-remove-report" class="btn btn-jbtn" value="삭제" onclick="deleteReport()">
                                <input type="button" id="btn-refresh-report" class="btn btn-jbtn" value="새로고침" onclick="refreshReport()">
                            </div>
                            <div class="subrb-body">
                                <table id="report-table" class="table table-striped" style="width: 100%;margin-top: 10px;">
                                    <thead>
                                    <tr>
                                        <th><input type="checkbox" id="report-check" onclick="onCheckboxes(this.id)"/></th>
                                        <th>날짜</th>
                                        <th>경로</th>
                                        <th>결과</th>
                                    </tr>
                                    </thead>
                                    <tbody>

                                    </tbody>
                                </table>
                            </div>
                        </div>

                    </div>


                </div>


            </div>
        </div>

    </div>
</div>
<%@ include file="../common/script.jsp" %>    <!-- common java script -->
<script src="/static/js/filescan/filescan.js"></script>
</body>
</html>