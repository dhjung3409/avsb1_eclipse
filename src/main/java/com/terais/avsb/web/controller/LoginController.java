package com.terais.avsb.web.controller;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jdk.nashorn.internal.ir.RuntimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.core.PropertiesData;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping(value="",method= RequestMethod.GET)
	public void rootHome(HttpServletRequest req,HttpServletResponse response,HttpSession session,Principal prin){
		logger.debug("root view");
		logger.debug(req.getSession().getServletContext().getRealPath("/"));
		logger.debug("Session timeout: "+session.getMaxInactiveInterval());
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

	@RequestMapping(value="login",method= RequestMethod.GET)
	public ModelAndView loginHome(HttpSession session, HttpServletResponse response,Principal prin){
		logger.debug("login view");				
		String name = prin==null?null:prin.getName();		
		logger.debug("name: "+name);
		try {
			if(name!=null)response.sendRedirect("/check");			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Login Home IOException: "+e.getMessage());
		}
		logger.debug("accountCount: "+PropertiesData.accountCount);
		logger.debug("InstallPath: "+PropertiesData.installPath);
		logger.debug("LoginStatus: "+PropertiesData.logStatus);
		logger.debug("Port: "+PropertiesData.port);
		logger.debug("UseAPI: "+PropertiesData.useApi);
		logger.debug("Worker: "+PropertiesData.worker);
		logger.debug("SubIp: "+PropertiesData.subIp.toString());
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		mav.addObject("msg","fail");		
		
		return mav;
	}

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
			}
		} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Login Not Success IOEception: "+e.getMessage());
		}
		logger.debug("success");
		return "page/dashboard";
	}

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

	@RequestMapping(value = "/fail", method = RequestMethod.GET)
	public String failAuthorities(){

		return "../../error/failAuth";
	}


}
