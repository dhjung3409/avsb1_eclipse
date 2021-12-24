package com.terais.avsb.web.controller;

import java.util.*;

import com.terais.avsb.module.IpListSortByIp;
import com.terais.avsb.service.impl.LoginServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.cron.CurrentCountScheduler;


/**
  * 기타 기능 컨트롤러
  */
@Controller
@RequestMapping("etc/*")
public class CommonController {

	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	/**
	 * bean 등록된 LoginServiceImpl 클래스 객체
	 */
	@Autowired
	private LoginServiceImpl loginService;

//	/**
//	  *
//	  * @return
//	  */
//	@RequestMapping(value = "/current/detect", method = RequestMethod.GET)
//	@ResponseBody
//	public List<Integer> getCheckMinute() {
//		List<Integer> list = null;
//		 list = CurrentCountScheduler.getCountList();
//		return list;
//	}

//	/**
//	  *
//	  * @return
//	  */
//	@RequestMapping(value = "/currnet/count/refresh", method = RequestMethod.POST)
//	public Map<String, Boolean> restCheck() {
//		Map<String, Boolean> map = null;
//		return map;
//	}
//
//	/**
//	  *
//	  * @return
//	  */
//	@RequestMapping(value = "/currnet/result/log", method = RequestMethod.GET)
//	public List<String> getCheckLog() {
//		List<String> log = null;
//		return log;
//	}

//	/**
//	  *
//	  * @return
//	  */
//	@RequestMapping(value = "/currnet/result/refresh", method = RequestMethod.POST)
//	public Map<String, Boolean> resetCheckLog() {
//		Map<String, Boolean> map = null;
//		return map;
//	}

	/**
	  * 등록된 IP 출력
	  * @return 등록된 IP
	  */
	@RequestMapping(value="subip",method = RequestMethod.GET)
	@ResponseBody
	public List<String> getLogIP(){
		logger.debug(PropertiesData.subIp.size()+"");

		List<String> ipList = new ArrayList<String>();
		for(String ip : PropertiesData.subIp){
			String ipInfo=ip.substring(ip.indexOf("$")+1);
			ipList.add(ipInfo);
		}
		Collections.sort(ipList, new IpListSortByIp());

		return ipList;

	}

	/**
	  * 로그인 계정의 유저 이름, 권한, 라이센스 기간 데이터
	  * @param auth 로그인한 계정 정보
	  * @return 유저 이름, 권한, 라이센스 기간
	  */
	@RequestMapping(value="license/user/check",method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getUserInfo(Authentication auth){
		logger.debug("principal: "+auth.getPrincipal().toString());
		logger.debug("authorities: "+auth.getAuthorities().toString());
		logger.debug("details: "+auth.getDetails().getClass());
		return loginService.getLoginUser(auth);
	}

//	/**
//	  *
//	  */
//	@RequestMapping(value="debug",method = RequestMethod.GET)
//	public void debugBreak(){
//		logger.info("stop");
//	}

//	/**
//	  *
//	  * @return
//	  */
//	@RequestMapping(value="status/check", method=RequestMethod.GET)
//	@ResponseBody
//	public boolean getLicenseStatus(){
//		boolean result = false;
//		result = PropertiesData.licenseStatus;
//		return result;
//	}

	/**
	  * 현재 시간 출력
	  * @return 현재 시
	  */
	@RequestMapping(value="clock/time", method = RequestMethod.GET)
	@ResponseBody
	public Date getServerTime(){
		return new Date();
	}
}
