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

/**
  * 환경 설정 컨트롤러
  */
@Controller
@RequestMapping("config/*")
public class ConfigController {

	private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

	/**
	 * bean 등록된 ConfigAPIServiceImpl 클래스 객체
	 */
	@Autowired
	private ConfigAPIServiceImpl configService;

	/**
	  * 환경 설정 페이지 뷰 포인트
	  * @return 환경 설정 페이지
	  */
	@RequestMapping(value="view", method=RequestMethod.GET)
	public ModelAndView configHome(){
		logger.debug("config view");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/config");
		mav.addObject("message","error");
		
		return mav;
	}


	/**
	  * 읽어낸 로그 파일 결과물을 저장해놓는 기간 설정
	  * @param rotate 저장 기간
	  */
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

	/**
	  * 스케줄러 검사 결과물 저장 개수 설정
	  * @param count 저장 개수
	  */
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

	/**
	  * 서버 등록
	  * @param server 등록할 서버 IP
	  * @param httpStatus http:// | https://
	  */
	@RequestMapping(value="server", method=RequestMethod.POST)
	public void addSubServer(@RequestParam String server,@RequestParam String httpStatus){
		try{
			logger.debug("server : "+server);
			boolean ipRes = RegularExpression.checkIP(server);
			boolean checkHttp = httpStatus.equals("http://")||httpStatus.equals("https://")?true:false;
			if(ipRes==true && checkHttp == true){
				String ip = httpStatus+"$"+server;
				logger.debug("Input IP");
				configService.setSubIP(ip);
			}
			logger.debug(PropertiesData.subIp.toString());
		}catch (NullPointerException err){
			logger.error("Null point error ["+server+"] => err : "+err);
		}catch (Exception err){
			logger.error("Wrong value exception ["+server+"] => err : "+err);
		}

	}

	/**
	  * 서버 목록 출력
	  * @return 서버 목록
	  */
	@RequestMapping(value="server/list", method=RequestMethod.GET)
	@ResponseBody
	public List<String[]> getServerList(){
		return configService.getSubIP();
	}

	@RequestMapping(value="server/port/change", method=RequestMethod.POST)
	public void setServerPort(@RequestParam String port){
		configService.setServerPort(port);
	}

	/**
	  * 서버 삭제
	  * @param delItems 삭제할 서버 리스트
	  */
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

	/**
	  * 감염현황 차트 리로드 시간 설정
	  * @param time 리로드 간격
	  */
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

	/**
	  * 실시간 차트 리로드 시간 설정
	  * @param time 리로드 간격
	  */
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

	/**
	  * 실시간 로그 리로드 시간 설정
	  * @param time 리로드 간격
	  */
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

}
