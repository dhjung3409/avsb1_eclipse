<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
<%@ include file="../common/css.jsp" %>    <!-- common css -->
<link rel="stylesheet" href="/static/css/config/config.css"><!-- config CSS -->
<%--<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
<title>AVSB - 설정 페이지</title>
</head>
<body>

<div id='body-wrapper'>

    <%@ include file="../common/top.jsp" %>

    <div id='content-wrapper'>

        <%@ include file="../common/menu.jsp" %>

        <div id='right-content-wrapper'>
            <div id='right-content'>
                <!-- config content -->
                <div id="config-wrapper">

                    <div id="dbInfo">
                        <div class="sub-title">
                            <label>데이터베이스 설정</label>
                        </div>
                        <div class="table-wrapper">
                            <table class="table table-bordered">
                                <colgroup>
                                    <col width="20%">
                                    <col width="80%">
                                </colgroup>

                                <tbody>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">로그 설정</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" id="records" name="records">
                                            <option value="30">30일</option>
                                            <option value="90">90일</option>
                                            <option value="180">180일</option>
                                            <option value="360">360일</option>
                                        </select>
                                        <input class="btn jbutton" id="btn-record" type="button" value="변경" onclick="rotateLogConfig()">
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="report-config">
                        <div class="sub-title">
                            <label>리포트 설정</label>
                        </div>
                        <div class="table-wrapper">
                            <table class="table table-bordered">
                                <colgroup>
                                    <col width="20%">
                                    <col width="80%">
                                </colgroup>

                                <tbody>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">최대 개수 설정</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" id="report-count">
                                            <option value="30" selected="selected">30개</option>
                                            <option value="60">60개</option>
                                            <option value="100">100개</option>
                                        </select>
                                        <input class="btn jbutton" id="btn-report" type="button" value="변경" onclick="configReport()">
                                        <h6 style="color:#e04040;font-size:10px;">최대개수가 넘는경우 자동 삭제됩니다.</h6>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                    </div>

                    <div id="serverInfo">
                        <div class="sub-title">
                            <label>서버 설정</label>
                        </div>
                        <div class="table-wrapper">
                            <table class="table table-bordered">
                                <colgroup>
                                    <col width="20%">
                                    <col width="80%">
                                </colgroup>

                                <tbody>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">서버 아이피</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" name="http" id="http-selected">
                                            <option value="">--------</option>
                                            <option value="http://">HTTP</option>
                                            <option value="https://">HTTPS</option>
                                        </select>
                                        <input class="form-control" type="text" id="server" name="server"
                                               maxlength="15">
                                        <input class="btn jbutton" id="btn-server" type="button" value="추가" onclick="addSubServer()">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">서버 리스트</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <table class="table table-bordered">
                                            <colgroup>
                                                <col width="5%">
                                                <col width="95%">
                                            </colgroup>
                                            <thead>
                                            <tr>
                                                <th class="cb-center">
                                                    <input type="checkbox" id="check-all" onclick="checkAll()">
                                                </th>
                                                <th>
                                                    <span>IP</span>
                                                </th>
                                            </tr>
                                            </thead>
                                            <tbody id="serverList">
                                                <!-- <tr>
                                                    <td>
                                                        <input type="checkbox" class="server" items="1">
                                                    </td>
                                                    <td>
                                                        <span>192.168.0.1</span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td>
                                                        <input type="checkbox" class="server" items="2">
                                                    </td>
                                                    <td>
                                                        <span>192.168.0.2</span>
                                                    </td>
                                                </tr> -->
                                            </tbody>
                                            <tfoot>
                                                <tr>
                                                    <td colspan="10">
                                                        <input type="button" class="btn jbutton" value="삭제" onclick="delServers()">
                                                    </td>
                                                </tr>
                                            </tfoot>
                                        </table>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="chartInfo">
                        <div class="sub-title">
                            <label>차트 설정(리로드 시간 설정)</label>
                        </div>
                        <div class="table-wrapper">
                            <table class="table table-bordered">
                                <colgroup>
                                    <col width="20%">
                                    <col width="80%">
                                </colgroup>

                                <tbody>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">감염현황 차트 리로드 시간</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" id="malware" name="malware">
                                            <option value="5">5분</option>
                                            <option value="10">10분</option>
                                            <option value="15">15분</option>
                                            <option value="30">30분</option>
                                            <option value="60">1시간</option>
                                        </select>
                                        <input class="btn jbutton" id="btn-malware" type="button" value="변경" onclick="changeMalwareChartRefreshTime()">
                                    </td>

                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">실시간 차트 리로드 시간</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" id="current" name="current">
                                            <option value="10">10초</option>
                                            <option value="20">20초</option>
                                            <option value="30">30초</option>
                                            <option value="40">40초</option>
                                            <option value="50">50초</option>
                                            <option value="60">60초</option>
                                        </select>
                                        <input class="btn jbutton" id="btn-current" type="button" value="변경" onclick="changeCurrentChartRefreshTime()">
                                    </td>
                                </tr>
                                <tr>
                                    <td style="text-align: center;">
                                        <span class="little-title">실시간 로그 리로드 시간</span>
                                    </td>
                                    <td style="padding-left: 40px;">
                                        <select class="form-control" id="current-log" name="current">
                                            <option value="5">5분</option>
                                            <option value="10">10분</option>
                                            <option value="15">15분</option>그
                                            <option value="30">30분</option>
                                            <option value="60">1시간</option>
                                        </select>
                                        <input class="btn jbutton" id="btn-current-log" type="button" value="변경" onclick="changeCurrentLogRefreshTime()">
                                    </td>

                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>


                </div>


            </div>
        </div>

    </div>
</div>
<%@ include file="../common/script.jsp" %>    <!-- common java script -->
<script src="/static/js/config/config.js"></script>
</body>
</html>