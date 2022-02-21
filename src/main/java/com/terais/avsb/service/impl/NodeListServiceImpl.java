package com.terais.avsb.service.impl;

import com.terais.avsb.core.CurrentLog;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.module.CheckOS;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.NodeListService;
import com.terais.avsb.vo.CurrentLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  * Local 서버의 악성코드 감염 현황, 실시간 검사 현황, 최근 로그 현황 데이터를 가져오는 클래스
  */
@Service
public class NodeListServiceImpl implements NodeListService{

	private static final Logger logger = LoggerFactory.getLogger(NodeListServiceImpl.class);


	/**
	  * Local 서버가 가지고 있는 악성코드 감염 현황 데이터를 가져오는 메소드
	  * @param period 표시할 데이터 기간
	  * @return Local 서버 악성코드 감염 현황
	  */
	public Map<String, String> getResultValue(String period) {
		Map<String, String> result = new HashMap<String,String>();
		Properties prop = new Properties();
		List<String> dates = getDateProp(period);
		int fail = 0;
		int disinfect = 0;
		int infect = 0;
		int normal = 0;
		int total = 0;
		File logFile = null;
		logger.debug(dates.toString());
		for(int i=0;i<dates.size();i++){

			String logFilePath = FilePath.repoFolder+ CheckOS.osSeparator+dates.get(i)+CheckOS.osSeparator+"result";
			logFile = new File(logFilePath);
			logger.debug("LogFile Length: "+logFile.length());
			if(logFile.exists()&&logFile.length()!=0) {
				prop = getProperties(logFile, prop);
				int propFail = 0;
				int propDisInfect = 0;
				int propInfect = 0;
				int propNormal= 0;
				int propTotal = 0;
				try {
					propFail = Integer.parseInt(prop.get("failed").toString());
					logger.debug("parse: " + fail);
					propDisInfect = Integer.parseInt(prop.get("disinfected").toString());
					logger.debug("disinfect: " + disinfect);
					propInfect = Integer.parseInt(prop.get("infected").toString());
					logger.debug("infect: " + infect);
					propNormal = Integer.parseInt(prop.get("normal").toString());
					logger.debug("normal: " + normal);
					propTotal = Integer.parseInt(prop.get("total").toString());
					logger.debug("total: " + total);
				}catch(NumberFormatException e){
					logger.error("Result Properties NumberFormatException: "+e.getMessage());
					prop.setProperty("failed","0");
					prop.setProperty("disinfected","0");
					prop.setProperty("infected","0");
					prop.setProperty("normal","0");
					prop.setProperty("total","0");
					PropertiesData.setProp(prop,logFilePath);
					continue;
				}
				fail = fail+propFail;
				logger.debug("fail2: "+fail);
				disinfect = propDisInfect+disinfect;
				infect = infect+propInfect;
				normal = normal+propNormal;
				total = total+propTotal;
			}
		}
		result.put("failed", String.valueOf(fail));
		result.put("disinfected", String.valueOf(disinfect));
		result.put("infected", String.valueOf(infect));
		result.put("normal", String.valueOf(normal));
		result.put("total",String.valueOf(total));
		logger.debug("total: "+result.get("total"));

		return result;
	}
	/**
	  * 금일 날짜를 기준으로 입력된 기간(일 , 주 , 월)에 맞춘 날짜 데이터를 가져오는 메소드
	  * @param period 지정된 기간
	  * @return 지정된 기간부터 오늘까지 날짜 데이터 리스트
	  */
	public List<String> getDateProp(String period){
		List<String> dates = new ArrayList<String>();

		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE,-getPeriodInteger(period));
		Date secondDate = cl.getTime();
		dates.add(sdf.format(secondDate));
		while(secondDate.compareTo(date)!=0){
			cl.add(Calendar.DATE,1);
			secondDate = cl.getTime();
			dates.add(sdf.format(secondDate));
			if(dates.size()>=31){
				break;
			}
		}

		return dates;
	}

	/**
	  * Properties 파일의 데이터를 가져오는 메소드
	  * @param logFile 데이터를 가져올 파일
	  * @param pro 데이터를 담아놓을 Properties 객체
	  * @return 데이터가 담긴 Properties 객체
	  */
	public Properties getProperties(File logFile,Properties pro){
		try {
			FileInputStream fis = new FileInputStream(logFile);
			pro.load(fis);
		} catch (IOException e) {
			logger.error("Get Properties IOException: "+e.getMessage());
		}

		return pro;
	}
	
	/**
	  * 입력된 기간 일 수를 가져오는 메소드
	  * @param period 기간
	  * @return 기간 일 수
	  */
	public int getPeriodInteger(String period){
		if(period.equals("weekly")){
			return 6;
		}else if(period.equals("monthly")){
			return countMonth();
		}else{
			return 0;
		}
	}
	
	/**
	  * 몇월인지에 따라 기간을 카운팅하는 메소드
	  * @return 한달 기간 일 수
	  */
	public int countMonth(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
		String[] split=sdf.format(date).split("/");
		int j = Integer.parseInt(split[0]);
		String yearCheck = split[1];
		int month = 0;
		if(j==5||j==7||j==10||j==12){
			month=29;
		}
		else if(j==3&&(Integer.parseInt(yearCheck))%4==0&&(Integer.parseInt(yearCheck))%400!=0){
			month=28;
		}
		else if(j==3||(Integer.parseInt(yearCheck))%400==0){
			month=27;
		}
		else {
			month=30;
		}
		return month;
	}

	/**
	  * Local 실시간 검사 현황 데이터를 가져오는 메소드
	  * @return Local 실시간 검사 현황 데이터
	  */
	public Map<String,Object> getCurrentChart(){
		Map<String,String> node = new HashMap<String,String>();
		Map<String,Object> chart = new HashMap<String, Object>();
		chart.putAll(node);
		chart.put("count",CurrentCountScheduler.getCountList());
		return chart;
	}

	/**
	  * Local 최근 검사 로그를 가져오는 메소드
	  * @param ip Local 서버의 IP
	  * @return 현재 Local 서버가 가지고 있는 최근 검사 로그와 서버의 IP
	  */
	public List<CurrentLogVO> getCurrentLastLog(String ip){
		File file = new File(FilePath.logPath);
		List<String> currentLog = CurrentLog.getCurrentLog(file);
		List<CurrentLogVO> list = new ArrayList<CurrentLogVO>();
		if(currentLog.size()>=1) {
			logger.debug("currentLog: "+currentLog.get(0));
			for (String current : currentLog) {
				CurrentLogVO logObject = new CurrentLogVO();
				String[] split = current.split(" ");
				int i=0;
				logObject.setIp(ip);
				logObject.setTime(split[0] + " " + split[1]);
				logObject.setPath(split[3].substring(split[3].indexOf("=")+1, split[3].indexOf(",")));
				logObject.setResult(split[8].split("=")[1].replace(",", " "));
				logger.debug("current result: " + split[8].split("=")[1]);
				list.add(logObject);
			}
		}else{

		}
		return list;
	}

}