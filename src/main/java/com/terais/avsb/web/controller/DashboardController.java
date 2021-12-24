package com.terais.avsb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.terais.avsb.vo.CurrentLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.NodeAndCurrentGetServiceImpl;
import com.terais.avsb.service.impl.NodeListServiceImpl;


/**
  * 대시보드 컨트롤러
  */
@Controller
@RequestMapping("dashboard/*")
public class DashboardController {

	private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

	/**
	 * bean 등록된 NodeListServiceImpl 클래스 객체
	 */
	@Autowired
	private NodeListServiceImpl nodeService;

	/**
	 * bean 등록된 NodeAndCurrentGetServiceImpl 클래스 객체
	 */
	@Autowired
	private NodeAndCurrentGetServiceImpl getService;


	/**
	  * 대시보드 페이지 뷰 포인트
	  * @return 대시보드 페이지
	  */
	@RequestMapping(value="view", method=RequestMethod.GET)
	public ModelAndView dashBoardHome(){
		ModelAndView mav = new ModelAndView();
		List<Map<String,String>> nodeList = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> chartList = new ArrayList<Map<String,Object>>();

		chartList.add(nodeService.getCurrentChart());
		mav.addObject(nodeList);
		mav.addObject(chartList);
		mav.setViewName("page/dashboard");

		return mav;
	}




	/**
	  * Local 악성코드 감염현황 차트 데이터 출력
	  * @param period 월 | 주 | 일
	  * @return Local 악성코드 감염현황 차트 데이터
	  */
	@RequestMapping(value="/rest/node",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,String> getNode(@RequestParam String period){
		logger.debug("getNode Start");
		return nodeService.getResultValue(period);
	}

	/**
	  * Local 실시간 검사 차트 데이터 출력
	  * @return Local 실시간 검사 차트 데이터
	  */
	@RequestMapping(value="/rest/chart",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getChart(){
		logger.debug("getChart Start");
		return nodeService.getCurrentChart();
	}

	/**
	  * Local 실시간 로그 데이터 출력
	  * @param ip 데이터를 출력하는 서버의 IP
	  * @return Local 실시간 로그 데이터
	  */
	@RequestMapping(value="/rest/last/log",method=RequestMethod.GET)
	@ResponseBody
	public List<CurrentLogVO> getCurrentLogList(@RequestParam String ip){
		logger.debug("getChart Start");
		return nodeService.getCurrentLastLog(ip);
	}

//	/**
//	  * @Method Name : getReloarChart
//	  * @작성일 : 2021. 12. 17.
//	  * @작성자 : DooHee Jung
//	  * @변경이력 : None
//	  * @Method 설명 :
//	  * @param reloadCount
//	  * @return
//	  */
//	@RequestMapping(value="/rest/chart/reload",method=RequestMethod.GET)
//	@ResponseBody
//	public Map<String,Object> getReloarChart(@RequestParam String reloadCount){
//		logger.debug("getChart Start");
//		return nodeService.getReloadChart(reloadCount);
//	}

	/**
	  * 등록된 서버들 중 연결 가능한 서버로부터 감염현황 차트 데이터 받아와 출력
	  * @param period 월 | 주 | 일
	  * @return 감염현황 차트 데이터
	  */
	@RequestMapping(value="/server/node",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getServersNode(@RequestParam String period){
		logger.debug("getServerNode Start");
		return getService.getNode(period);
	}
	/**
	  * 등록된 서버들 중 연결 가능한 서버로부터 실시간 검사 현황 데이터 받아와 출력
	  * @return 실시간 검사 현황 데이터
	  */
	@RequestMapping(value="/server/chart",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getServersChart(){
		logger.debug("getServerChart Start");
		return getService.getChart();
	}
	
//	/**
//	  * @Method Name : getServersReloadChart
//	  * @작성일 : 2021. 12. 17.
//	  * @작성자 : DooHee Jung
//	  * @변경이력 : None
//	  * @Method 설명 :
//	  * @return
//	  */
//	@RequestMapping(value="/server/chart/reload",method=RequestMethod.GET)
//	@ResponseBody
//	public List<Object> getServersReloadChart(){
//		logger.debug("getServerChart Start");
//		return getService.getChart("chart/reload","1");
//	}

	/**
	  * 등록된 서버들 중 연결 가능한 서버로부터 최근 로그 데이터 받아와 출력
	  * @return 최근 로그 데이터
	  */
	@RequestMapping(value="/server/last/line",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getServersCurrentLine(){
		logger.debug("getServerNode Start");
		return getService.getLastLine();
	}

}