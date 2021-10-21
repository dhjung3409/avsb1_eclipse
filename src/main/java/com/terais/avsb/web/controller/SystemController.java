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

@Controller
@RequestMapping("system/*")
public class SystemController {

	private static final Logger logger = LoggerFactory.getLogger(SystemController.class);
	
	@Autowired
	private SystemInfoServiceImpl systemInfo;

	@RequestMapping(value="", method=RequestMethod.GET)
	public ModelAndView systemHome() {
		logger.debug("system view");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/system");
		mav.addObject("message", "error");

		return mav;
	}
	
	@RequestMapping(value="/rest/server/info", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getInfoSystem(){
		return systemInfo.getServerInfo();
	}

	@RequestMapping(value="/rest/server/status", method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getIPStatus(){		
		return systemInfo.getServerList();
	}

	@RequestMapping(value="/rest/server/engine", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getEngineInfoSystem(){		
		return systemInfo.getEngineInfo();
	}
	
	@RequestMapping(value="/rest/server/avsb", method=RequestMethod.GET)
	@ResponseBody
	public Map<Object, Object> getAVSBInfoSystem(){		
		return systemInfo.getAVSBInfo();
	}
	
	@RequestMapping(value="/rest/ping", method=RequestMethod.GET)
	public ModelAndView getConnect(){
		return null;
	}
	
}
