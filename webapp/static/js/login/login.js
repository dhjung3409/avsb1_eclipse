
function loginProcess(){
    var id = document.getElementById('userId');
    var pw = document.getElementById('userPw');
    var loginForm = document.getElementById('login-form');
    
    if(id.value==""){
      alert("아이디를 입력하세요.");
      id.focus();
      return false;
    }else if(pw.value==""){
      alert("비밀번호를 입력하세요.");
      pw.focus();
      return false;
    }else{
        document.loginForm.action="/memberLogin";
        document.loginForm.submit();
    }
}

function loginInit(){
}

loginInit();