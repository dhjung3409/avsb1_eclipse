package com.terais.avsb.web.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.SystemInfoServiceImpl;

/**
  * 시스템 컨트롤러
  */
@Controller
@RequestMapping("system/*")
public class SystemController {

	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
	
	/**
	 * bean 등록된 SystemInfoServiceImpl 클래스 객체
	 */
	@Autowired
	private SystemInfoServiceImpl systemInfo;

	/**
	  * 시스템 페이지 뷰 포인트
	  * @return 시스템 페이지
	  */
	@RequestMapping(value="", method=RequestMethod.GET)
	public ModelAndView systemHome() {
		logger.debug("system view");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/system");
		mav.addObject("message", "error");

		return mav;
	}
	
	/**
	  * 서버 OS 정보 출력
	  * @return 사버 OS 정보
	  */
	@RequestMapping(value="/rest/server/info", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getInfoSystem(){
		return systemInfo.getServerInfo();
	}

	/**
	  * 등록된 서버 IP 출력
	  * @return 등록된 서버 IP
	  */
	@RequestMapping(value="/rest/server/status", method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getIPStatus(){		
		return systemInfo.getServerList();
	}

	/**
	  * 엔진 정보 출력
	  * @return 엔진 정보
	  */
	@RequestMapping(value="/rest/server/engine", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getEngineInfoSystem(){		
		return systemInfo.getEngineInfo();
	}
	
	/**
	  * AVSB1 버전 및 라이센스 기간 출력
	  * @return AVSB1 버전 및 라이센스 기간
	  */
	@RequestMapping(value="/rest/server/avsb", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getAVSBInfoSystem(){		
		return systemInfo.getAVSBInfo();
	}
	
	/**
	  * 서버 연결 확인용 매핑
	  * @return null
	  */
	@RequestMapping(value="/rest/ping", method=RequestMethod.GET)
	public ModelAndView getConnect(){
		return null;
	}
	
}
