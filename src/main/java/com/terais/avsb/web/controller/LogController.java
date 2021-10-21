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



@Controller
@RequestMapping("log/*")
public class LogController {

	private static final Logger logger = LoggerFactory.getLogger(LogController.class);

	@Autowired
	private ModuleServiceImpl moduleService;

	@Autowired
	private PageLogServiceImpl pageLog;
	
	@Autowired
	private SaveLogFileServiceImpl logService;

	@RequestMapping("view")
	public ModelAndView logHome(){
		logger.debug("log view");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/log");
		mav.addObject("message","error");
		
		return mav;
	}

	@RequestMapping(value = "/result/{year}", method = RequestMethod.GET)
	@ResponseBody
	public ResultValue getYearResult(@PathVariable("year") String year){		
		ResultValue rv = new ResultValue();		
		try{						
			String resultPath = FilePath.getDatePath(year);	
			rv = moduleService.getTotal(resultPath, rv);
			logger.debug("getTotal End: "+rv);
		}catch(Exception e){
			logger.error("Error day: "+e);
		}
		return rv;
	}

	@RequestMapping(value = "/result/{year}/{month}", method = RequestMethod.GET)
	@ResponseBody
	public ResultValue getMonthResult(@PathVariable("year") String year,@PathVariable("month") String month) {
		ResultValue rv = new ResultValue();
		try{
			String resultPath = FilePath.getDatePath(year,month);
			rv = moduleService.getTotal(resultPath, rv);
			logger.debug("getTotal End: "+rv);
		}catch(Exception e){
			logger.error("Error day: "+e);
		}
		return rv;
	}

	@RequestMapping(value = "/result/{year}/{month}/{day}", method = RequestMethod.GET)	
	@ResponseBody
	public ResultValue getDayResult(@PathVariable("year") String year,@PathVariable("month") String month,@PathVariable("day") String day){
		ResultValue rv = new ResultValue();
		try{			
			String resultPath = FilePath.getDatePath(year,month,day);		
			rv = moduleService.getTotal(resultPath, rv);
			logger.debug("getTotal End: "+rv);
		}catch(Exception e){
			logger.error("Error day: "+e);
		}
		
		return rv;
	}

	@RequestMapping(value="/rest/resultlog",method = RequestMethod.GET)
	@ResponseBody
	public List<ReadLog> getLogList(){
		List<ReadLog> resultLog = pageLog.getPageLog();
		return resultLog;
	}

	@RequestMapping(value="list",method = RequestMethod.GET)
	@ResponseBody
	public List<Object> getLogList(@RequestParam(required=true) String ip) { //나중에 getLogList로 이름바꿀것
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
