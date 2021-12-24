package com.terais.avsb.web.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;



/**
  * 로그인 컨트롤러
  */
@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	/**
	  * 루트 페이지 연결 시 접속 상태에 따라 로그인 페이지, 대시보드 페이지로 연결시켜주는 메소드
	  * @param response HttpServletResponse
	  * @param prin 로그인 계정 정보
	  */
	@RequestMapping(value="",method= RequestMethod.GET)
	public void rootHome(HttpServletResponse response,Principal prin){
		logger.debug("root view");
		String name = prin==null?null:prin.getName();	
		try {
			if(name!=null){
				response.sendRedirect("/check");			
			}
			else{
				response.sendRedirect("/login");
			}
		} catch (IOException e) {
			logger.error("root view error");
		}
	}

	/**
	  * 로그인 페이지 뷰 포인트
	  * @param response HttpServletResponse
	  * @param prin 로그인 계정 정보
	  * @return 로그인 페이지
	  */
	@RequestMapping(value="login",method= RequestMethod.GET)
	public ModelAndView loginHome(HttpServletResponse response,Principal prin){
		logger.debug("login view");				
		String name = prin==null?null:prin.getName();		
		logger.debug("name: "+name);
		try {
			if(name!=null)response.sendRedirect("/check");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Login Home IOException: "+e.getMessage());
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		mav.addObject("msg","fail");		
		
		return mav;
	}

	/**
	  * 로그인 성공 페이지 뷰 포인트
	  * @param response HttpServletResponse
	  * @param prin 로그인 계정 정보
	  * @param sch SecurityContextHolder
	  * @return 대시보드 페이지
	  */
	@RequestMapping(value = "success", method = RequestMethod.GET)
	public String loginSuccess(HttpServletResponse response,Principal prin,SecurityContextHolder sch) {
		String name = prin==null?null:prin.getName();

		logger.debug("name: "+name);
		String auth = String.valueOf(sch.getContext().getAuthentication().getAuthorities().toArray()[0]);
		logger.debug("Success Auth: "+auth);
		try {
			if(name==null){
				response.sendRedirect("/login");			
			}else if(auth.equals("Role_expired")){
				response.sendRedirect("/fail");
			}else{

			}
		} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Login Not Success IOEception: "+e.getMessage());
		}
		logger.debug("success");
		return "page/dashboard";
	}

	/**
	  * 루트 uri 리다이렉트 페이지
	  * @param response HttpServletResponse
	  * @param prin 로그인 계정 정보
	  * @param sch SecurityContextHolder
	  * @return 대시보드 페이지
	  */
	@RequestMapping(value = "/check", method = RequestMethod.GET)
	public String checkLogin(HttpServletResponse response, Principal prin, SecurityContextHolder sch) {
		String name = prin==null?null:prin.getName();		
		logger.debug("name: "+name);

		String auth = String.valueOf(sch.getContext().getAuthentication().getAuthorities().toArray()[0]);
		logger.debug("Check Auth: "+auth);
		try {
			if(name==null){
				logger.debug("if start");
				response.sendRedirect("/login");
			}else if(auth.equals("Role_expired")){
				response.sendRedirect("/fail");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Check Login IOEception: "+e.getMessage());
		}
		return "page/dashboard";
	}

	/**
	  * 로그인 실패 페이지 뷰 포인트
	  * @return 로그인 실패 페이지
	  */
	@RequestMapping(value = "login_failed", method=RequestMethod.GET)
	public String rootHome2(){
		logger.debug("login failed");
		return "login_fail";
	}

	/**
	  * 라이센스 만료 페이지 뷰 포인트
	  * @return 라이센스 만료 페이지
	  */
	@RequestMapping(value = "/fail", method = RequestMethod.GET)
	public String failAuthorities(){

		return "../../error/failAuth";
	}


}
