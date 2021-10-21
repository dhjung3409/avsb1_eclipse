<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page session="true" %>

<!DOCTYPE html>
<html>
<%@ include file="../common/css.jsp" %>    <!-- common css -->
<link rel="stylesheet" href="/static/css/dashboard/dashboard.css"><!-- dashboard CSS -->
<%--<script src="/static/plugins/jquery/jquery-1.12.4.js"></script>--%>
<%--<script src="/static/plugins/bootstrap/js/bootstrap.min.js"></script>--%>
<title>AVSB - 대시보드 페이지</title>
</head>
<body>

<div id='body-wrapper'>

    <%@ include file="../common/top.jsp" %>

    <div id='content-wrapper'>

        <%@ include file="../common/menu.jsp" %>

        <div id='right-content-wrapper'>
            <div id='right-content'>
                <!-- dashboard content -->
                <div id="canvas-wrapper">
                    <div class="jbox">
                        <div id="malware-chart-wrapper">
                            <div class="btn-wrapper">
                                <select id="period-select" class="jbtn" onchange="onChangeTerms(this.value)">
                                    <option value="daily">일</option>
                                    <option value="weekly">주</option>
                                    <option value="monthly">월</option>
                                </select>
                                <button type="button" class="jbtn" onclick="refreshMalware()">새로고침</button>
                            </div>
                            <canvas id="malchart" style="width: 500px;height: 130px;"></canvas>
                        </div>
                        <div id="current-log-wrapper">
                            <table class="table table-striped" id="currentLog">
                                <thead>
                                    <tr>
<%--                                        <th colspan="4" style="color: #5a5959">최근 로그 현황<small>(30 Rows)</small></th>--%>
                                        <th colspan="4" style="color: #5a5959">최근 로그 현황</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>IP</td>
                                        <td>Time</td>
                                        <td>Path</td>
                                        <td>Result</td>
                                    </tr>
<%--                                    indexOf("/",20)--%>
<%--                                    <tr><td>127.0.0.1</td><td>11:12:30</td><td>/home/terais/codedns/base/dev/file.txt</td><td>Normal</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/dev/file.txt</td><td>Normal</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/dev/file.txt</td><td>Normal</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/dev/file.txt</td><td>Normal</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/dev/file.txt</td><td>Normal</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/.../upload.csv</td><td>Infect</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/.../upload.csv</td><td>Infect</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/.../upload.csv</td><td>Infect</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/.../upload.csv</td><td>Infect</td></tr>--%>
<%--                                    <tr><td>127.0.0.1</td><td>2021-10-15</td><td>/home/terais/codedns/base/.../upload.csv</td><td>Infect</td></tr>--%>

                                </tbody>
                            </table>
                        </div>
                    </div>

                    <div id="current-chart-wrapper">
                        <div class="btn-wrapper">
                            <button type="button" class="jbtn" onclick="refreshCurrentChart()">새로고침</button>
                        </div>
                        <canvas id="curchart" style="width: 500px;height: 130px;"></canvas>
                    </div>
                </div>


            </div>
        </div>

    </div>
</div>
<%@ include file="../common/script.jsp" %>    <!-- common java script -->
<script src="/static/plugins/chart/Chart.js"></script>
<script src="/static/js/dashboard/malwareChart.js"></script>
<script src="/static/js/dashboard/currentChart.js"></script>
<script src="/static/js/dashboard/currentLog.js"></script>
<script src="/static/js/dashboard/dashboard.js"></script>
</body>
</html>