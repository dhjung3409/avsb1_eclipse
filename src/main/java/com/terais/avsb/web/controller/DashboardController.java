package com.terais.avsb.web.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.terais.avsb.cron.SubIPCheckSchduler;
import com.terais.avsb.vo.CurrentLogVO;
import com.terais.avsb.vo.LoginVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.NodeAndCurrentGetServiceImpl;
import com.terais.avsb.service.impl.NodeListServiceImpl;


@Controller
@RequestMapping("dashboard/*")
public class DashboardController {

	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	@Autowired
	private NodeListServiceImpl nodeService;

	@Autowired
	private NodeAndCurrentGetServiceImpl getService;


	@RequestMapping(value="view", method=RequestMethod.GET)
	public ModelAndView dashBoardHome(Authentication auth){
		logger.debug("dashboard view");
		ModelAndView mav = new ModelAndView();
		List<Map<String,String>> nodeList = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> chartList = new ArrayList<Map<String,Object>>();

		chartList.add(nodeService.getCurrentChart());
		mav.addObject(nodeList);
		mav.addObject(chartList);
		mav.setViewName("page/dashboard");

		return mav;
	}




	@RequestMapping(value="/rest/node",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,String> getNode(@RequestParam String period){
		logger.debug("getNode Start");
		return nodeService.getSumNode(period);
	}

	@RequestMapping(value="/rest/chart",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getChart(){
		logger.debug("getChart Start");
		return nodeService.getCurrentChart();
	}

	@RequestMapping(value="/rest/last/log",method=RequestMethod.GET)
	@ResponseBody
	public List<CurrentLogVO> getCurrentLogList(@RequestParam String ip){
		logger.debug("getChart Start");
		return nodeService.getCurrentLastLog(ip);
	}

	@RequestMapping(value="/rest/chart/reload",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getReloarChart(@RequestParam String reloadCount){
		logger.debug("getChart Start");
		return nodeService.getReloadChart(reloadCount);
	}

	@RequestMapping(value="/server/node",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getServersNode(@RequestParam String period){
		logger.debug("getServerNode Start");
		return getService.getNode(period);
	}
	@RequestMapping(value="/server/chart",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getServersChart(){
		logger.debug("getServerChart Start");
		return getService.getChart("chart","0");
	}
	@RequestMapping(value="/server/chart/reload",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getServersReloadChart(){
		logger.debug("getServerChart Start");
		return getService.getChart("chart/reload","1");
	}

	@RequestMapping(value="/server/last/line",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getServersCurrentLine(){
		logger.debug("getServerNode Start");
		return getService.getLastLine();
	}

}