<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page isErrorPage="true" %>    
    
<%
    response.setStatus(200);
%>
    
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>잘못된 파라미터</title>
</head>
<script src="/static/plugins/jquery/jquery-1.12.4.js"></script><!-- jQuery -->
<script type="text/javascript">


var time = 5;

$(function() {
    setInterval(function() {
        time--;
        $("#clock2").text("5초후 다시 연결합니다.. 대기시간 : "+time+"초");
    }, 1000);
});

setTimeout("location.href='/'", 5000);

</script>

<body>

잘못된 접근입니다. <br><div id='clock2'></div>


</body>
</html>