package com.terais.avsb.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.terais.avsb.dto.ReadLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.RegularExpression;
import com.terais.avsb.dto.ResultValue;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.impl.ModuleServiceImpl;
import com.terais.avsb.service.impl.PageLogServiceImpl;
import com.terais.avsb.service.impl.SaveLogFileServiceImpl;



/**
  * 로그 뷰어 컨트롤러
  */
@Controller
@RequestMapping("log/*")
public class LogController {

	private static final Logger logger = LoggerFactory.getLogger(LogController.class);

//	/**
//	 * @FieldName : moduleService - [ModuleServiceImpl]
//	 */
//	@Autowired
//	private ModuleServiceImpl moduleService;

	/**
	 * bean 등록된 PageLogServiceImpl 클래스 객체
	 */
	@Autowired
	private PageLogServiceImpl pageLog;
	
	/**
	 * bean 등록된 SaveLogFileServiceImpl 클래스 객체
	 */
	@Autowired
	private SaveLogFileServiceImpl logService;

	/**
	  * 로그 페이지 뷰 포인트
	  * @return 로그 페이지
	  */
	@RequestMapping("view")
	public ModelAndView logHome(){
		logger.debug("log view");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/log");
		mav.addObject("message","error");
		
		return mav;
	}

//	/**
//	  * @Method Name : getYearResult
//	  * @작성일 : 2021. 12. 17.
//	  * @작성자 : DooHee Jung
//	  * @변경이력 : None
//	  * @Method 설명 :
//	  * @param year
//	  * @return
//	  */
//	@RequestMapping(value = "/result/{year}", method = RequestMethod.GET)
//	@ResponseBody
//	public ResultValue getYearResult(@PathVariable("year") String year){
//		ResultValue rv = new ResultValue();
//		try{
//			String resultPath = FilePath.getDatePath(year);
//			rv = moduleService.getTotal(resultPath, rv);
//			logger.debug("getTotal End: "+rv);
//		}catch(Exception e){
//			logger.error("Error day: "+e);
//		}
//		return rv;
//	}
//
//	/**
//	  * @Method Name : getMonthResult
//	  * @작성일 : 2021. 12. 17.
//	  * @작성자 : DooHee Jung
//	  * @변경이력 : None
//	  * @Method 설명 :
//	  * @param year
//	  * @param month
//	  * @return
//	  */
//	@RequestMapping(value = "/result/{year}/{month}", method = RequestMethod.GET)
//	@ResponseBody
//	public ResultValue getMonthResult(@PathVariable("year") String year,@PathVariable("month") String month) {
//		ResultValue rv = new ResultValue();
//		try{
//			String resultPath = FilePath.getDatePath(year,month);
//			rv = moduleService.getTotal(resultPath, rv);
//			logger.debug("getTotal End: "+rv);
//		}catch(Exception e){
//			logger.error("Error day: "+e);
//		}
//		return rv;
//	}
//
//	/**
//	  *
//	  * @param year
//	  * @param month
//	  * @param day
//	  * @return
//	  */
//	@RequestMapping(value = "/result/{year}/{month}/{day}", method = RequestMethod.GET)
//	@ResponseBody
//	public ResultValue getDayResult(@PathVariable("year") String year,@PathVariable("month") String month,@PathVariable("day") String day){
//		ResultValue rv = new ResultValue();
//		try{
//			String resultPath = FilePath.getDatePath(year,month,day);
//			rv = moduleService.getTotal(resultPath, rv);
//			logger.debug("getTotal End: "+rv);
//		}catch(Exception e){
//			logger.error("Error day: "+e);
//		}
//
//		return rv;
//	}

	/**
	  * 이상이 있는 로그 목록 출력
	  * @return 이상이 있는 로그 목록
	  */
	@RequestMapping(value="/rest/resultlog",method = RequestMethod.GET)
	@ResponseBody
	public List<ReadLog> getLogList(){
		List<ReadLog> resultLog=null;
		try {
			resultLog = pageLog.getPageLog();
			logger.debug("resultLog: "+resultLog.toString());
		}catch(Exception e){
			logger.error("getLogList Exception: "+e.getMessage());
		}
		logger.debug("resultLog try end");
		return resultLog;
	}

	/**
	  * 입력된 IP 서버가 지니고 있는 이상이 있는 로그 출력
	  * @param ip 출력할 로그를 가지고 있는 IP
	  * @return 입력된 서버가 지니고 있는 이상이 있는 로그
	  */
	@RequestMapping(value="list",method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getLogList(@RequestParam(required=true) String ip) {
		logger.debug("Input IP: " + ip);
		List<Object> result = null;
		result = new ArrayList<Object>();
		
		try {
			logger.debug("RegularExpression start");
			if (!RegularExpression.checkIP(ip)) {
				logger.debug("Wrong IP");
				return result;
			}
			logger.debug("RegularExpression pass");
			result = logService.getSaveLog(ip);
			logger.debug("this is work1");
			return result;

		} catch (Exception err) {
			logger.error("Get Log List Exception: "+err.getMessage());
			return result;
		}

	}

}
