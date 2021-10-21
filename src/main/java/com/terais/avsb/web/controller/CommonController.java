package com.terais.avsb.web.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.cron.CurrentCountScheduler;


@Controller
@RequestMapping("etc/*")
public class CommonController {

	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);

	@RequestMapping(value = "/current/detect", method = RequestMethod.GET)
	@ResponseBody
	public List<Integer> getCheckMinute() {
		List<Integer> list = null;
		 list = CurrentCountScheduler.getCountList();
		return list;
	}

	@RequestMapping(value = "/currnet/count/refresh", method = RequestMethod.POST)
	public Map<String, Boolean> restCheck() {

		Map<String, Boolean> map = null;

		return map;
	}

	@RequestMapping(value = "/currnet/result/log", method = RequestMethod.GET)
	public List<String> getCheckLog() {

		List<String> log = null;

		return log;
	}

	@RequestMapping(value = "/currnet/result/refresh", method = RequestMethod.POST)
	public Map<String, Boolean> resetCheckLog() {

		Map<String, Boolean> map = null;

		return map;
	}

	@RequestMapping(value="subip",method = RequestMethod.GET)
	@ResponseBody
	public Set<String> getLogIP(){
		logger.debug(PropertiesData.subIp.size()+"");
		return PropertiesData.subIp;	

	}

	@RequestMapping(value="debug",method = RequestMethod.GET)
	public void debugBreak(){
		logger.info("stop");
	}
}
