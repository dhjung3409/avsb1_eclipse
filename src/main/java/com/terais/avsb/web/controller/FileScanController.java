package com.terais.avsb.web.controller;

import java.util.List;
import java.util.Map;

import com.terais.avsb.core.ScanScheduleList;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.impl.ReadSchedulerServiceImpl;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.LocalScanSchedulerServiceImpl;

@Controller
@RequestMapping("filescan/*")
public class FileScanController {

	private static final Logger logger = LoggerFactory.getLogger(FileScanController.class);
	
	@Autowired
	private LocalScanSchedulerServiceImpl scanService;

	@Autowired
	private ReadSchedulerServiceImpl readSchduler;

	@RequestMapping(value = "view", method = RequestMethod.GET)
	public ModelAndView filescanHome() {
		logger.debug("filescan view");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/filescan");
		mav.addObject("message", "error");

		return mav;
	}

	@RequestMapping(value = "/report/result",method = RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getReportResult(@RequestParam String no){
		return readSchduler.getReportText(no);
	}

	@RequestMapping(value = "scheduler", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addScheduler(@RequestParam Map<String,String> dateData) {
		logger.debug("dateData: "+dateData.toString());
		Map<String, Object> result = null;
		try {
			logger.debug("path : " + dateData.get("path"));
			result = scanService.checkFile(dateData);
			logger.debug("add Schedule Result: "+result);
		} catch (NullPointerException err) {
			logger.error("Null point Error [" + dateData.get("path") + "] => err : " + err);
		} catch (Exception err) {
			logger.error("Wrong value exception [" + dateData.get("path") + "] => err : " + err);
		}
		return result;
	}

	@RequestMapping(value = "scheduler", method = RequestMethod.GET)
	@ResponseBody
	public List<ScanSchedule> getSchedulers() {
		logger.debug("getScheduler");
		return ScanScheduleList.scanSchedule;

	}

	@RequestMapping(value = "scheduler/delete", method = RequestMethod.POST)
	@ResponseBody
	public void deleteSchedulers(@RequestParam(value = "items[]") List<String> delItems) {

		try {
			logger.debug("delList : " + delItems.toString());
			scanService.deleteScanScheduler(delItems, FilePath.scheduler, ScanScheduleList.scanSchedule);
		} catch (NullPointerException err) {
			logger.error("Null point Error [" + delItems + "] => err : " + err);
		} catch (Exception err) {
			logger.error("Wrong value exception [" + delItems + "] => err : " + err);
		}

	}

	@RequestMapping(value = "report", method = RequestMethod.GET)
	@ResponseBody
	public List<ScanSchedule> getReports() {
		return ScanScheduleList.scanReport;
	}

	@RequestMapping(value = "report/delete", method = RequestMethod.POST)
	@ResponseBody
	public void deleteReports(@RequestParam(value = "items[]") List<String> delItems) {

		try {
			logger.debug("delList : " + delItems);
			scanService.deleteScanScheduler(delItems, FilePath.report, ScanScheduleList.scanReport);
		} catch (NullPointerException err) {
			logger.error("Null point Error [" + delItems + "] => err : " + err);
		} catch (Exception err) {
			logger.error("Wrong value exception [" + delItems + "] => err : " + err);
		}

	}
}