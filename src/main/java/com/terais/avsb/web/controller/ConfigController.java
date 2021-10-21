package com.terais.avsb.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.RegularExpression;
import com.terais.avsb.service.impl.ConfigAPIServiceImpl;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("config/*")
public class ConfigController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

	@Autowired
	private ConfigAPIServiceImpl configService;

	@RequestMapping(value="view", method=RequestMethod.GET)
	public ModelAndView configHome(){
		logger.debug("config view");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/config");
		mav.addObject("message","error");
		
		return mav;
	}


	@RequestMapping(value="log", method=RequestMethod.POST)
	public void configLogRotate(@RequestParam String rotate){

		try{
			int rotateVal = Integer.parseInt(rotate);

			if(rotateVal == 30 || rotateVal == 90 || rotateVal == 180 || rotateVal == 360){
				logger.debug("Normal value.");
				logger.debug("rotate : "+rotate);
				configService.setDateTerm(rotate);
				logger.debug(PropertiesData.dateTerm);

			}else{
				logger.error("Log Config value change to wrong value: " + rotate);
			}

		}catch (NumberFormatException err){
			logger.error("Number format error ["+rotate+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+rotate+"] => err : "+err);
		}

	}

	@RequestMapping(value="report", method=RequestMethod.POST)
	public void configReportCount(@RequestParam String count){

		try{
			int countVal = Integer.parseInt(count);
			if(countVal == 30 || countVal == 60 || countVal == 100){
				logger.debug("Normal value.");
				logger.debug("count : "+countVal);
				configService.setReportCount(count);
			}else {
				logger.error("Report reload value change to wrong value: " + count);
			}

		}catch (NumberFormatException err){
			logger.error("Number format error ["+count+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+count+"] => err : "+err);
		}

	}

	@RequestMapping(value="server", method=RequestMethod.POST)
	public void addSubServer(@RequestParam String server){

		try{

			logger.debug("server : "+server);
			boolean ipRes = RegularExpression.checkIP(server);
			if(ipRes==true){
				logger.debug("Input IP");
				configService.setSubIP(server);
			}
			logger.debug(PropertiesData.subIp.toString());


		}catch (NullPointerException err){
			logger.error("Null point error ["+server+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+server+"] => err : "+err);
		}

	}

	@RequestMapping(value="serverlist", method=RequestMethod.POST)
	public void delServers(@RequestParam(value="items[]") List<String> delItems){

		try{
			logger.debug("delServer List : "+delItems);
			configService.delSubIP(delItems);
		}catch (NumberFormatException err){
			logger.error("Number format error ["+delItems.toString()+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+delItems.toString()+"] => err : "+err);
		}

	}

	@RequestMapping(value="malchart/time", method=RequestMethod.POST)
	public void modifyMalwareChartTime(@RequestParam String time){

		try{
			int reloadTime = Integer.parseInt(time);

			if(reloadTime == 5 || reloadTime == 10 || reloadTime == 15 || reloadTime == 30 || reloadTime == 60){
				logger.debug("Normal value.");
				logger.debug("malware reloadTime : "+reloadTime);
				configService.setReloadTime(time);
				logger.debug(PropertiesData.resReloadTime);

			}else{
				logger.error("Malware reload value change to wrong value: "+reloadTime);
			}

		}catch (NumberFormatException err){
			logger.error("Number format error ["+time+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+time+"] => err : "+err);
		}

	}

	@RequestMapping(value="current/time", method=RequestMethod.POST)
	public void modifyCurrentChartTime(@RequestParam String time){

		try{
			int reloadTime = Integer.parseInt(time);

			if(reloadTime == 10 || reloadTime == 20 || reloadTime == 30 || reloadTime == 40 || reloadTime == 50|| reloadTime == 60){
				logger.debug("Normal value.");
				logger.debug("Current reloadTime : "+reloadTime);
				configService.setCurrentReloadTime(time);
				logger.debug(PropertiesData.currentReloadTime);

			}else{
				logger.error("Malware reload value change to wrong value: "+reloadTime);
			}

		}catch (NumberFormatException err){
			logger.error("Number format error ["+time+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+time+"] => err : "+err);
		}

	}

	@RequestMapping(value="current/log", method=RequestMethod.POST)
	public void modifyCurrentLogTime(@RequestParam String time){
		try{
			int reloadTime = Integer.parseInt(time);

			if(reloadTime == 5 || reloadTime == 10 || reloadTime == 15 || reloadTime == 30 || reloadTime == 60){
				logger.debug("Normal value.");
				logger.debug("current Log reloadTime : "+reloadTime);
				configService.setLogReloadTime(time);
				logger.debug(PropertiesData.logReloadTime);

			}else{
				logger.error("Malware reload value change to wrong value: "+reloadTime);
			}

		}catch (NumberFormatException err){
			logger.error("Number format error ["+time+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+time+"] => err : "+err);
		}
	}

	@RequestMapping(value="/rest/update/radio",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, List<Object>> sendUpdateFileInfo(){
		return configService.getUpdateFile();
	}

	@RequestMapping(value="/rest/update/info",method=RequestMethod.POST)
	@ResponseBody
	public boolean getUpdateFileInfo(@RequestParam String fileName, @RequestParam long fileSize, Principal principal){
		logger.info("Upload File Name: "+fileName+", File Size: "+fileSize);
		return configService.saveFileInfo(fileName,fileSize,principal);
	}

	@RequestMapping(value="/rest/update/files",method=RequestMethod.POST)
	@ResponseBody
	public int getUpdateFiles(MultipartHttpServletRequest req){
		logger.info("update File");
		return configService.fileTest(req);
	}

	@RequestMapping(value="/rest/update/real_time",method=RequestMethod.POST)
	@ResponseBody
	public List<Object> getUpdateFiles(@RequestParam int num){
		logger.info("update File");
		return configService.updateEngine(num);
	}
}
