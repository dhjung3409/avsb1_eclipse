package com.terais.avsb.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.ServletContext;

import com.terais.avsb.core.CurrentLog;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.dto.Current;
import com.terais.avsb.module.LicenseCheck;
import com.terais.avsb.vo.CurrentLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.NodeListService;

@Service
public class NodeListServiceImpl implements NodeListService{

	private static final Logger logger = LoggerFactory.getLogger(NodeListServiceImpl.class);


	@Autowired
	private ServletContext servletContext;

	public Map<String, String> getResultValue(String period) {
		logger.debug("context: "+servletContext.getRealPath("/resource"));
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

			String logFilePath = FilePath.repoFolder+"/"+dates.get(i)+"/result";
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

	public Properties getProperties(File logFile,Properties pro){
		try {
			FileInputStream fis = new FileInputStream(logFile);
			pro.load(fis);
		} catch (IOException e) {
			logger.error("Get Properties IOException: "+e.getMessage());
		}

		return pro;
	}
	public int getPeriodInteger(String period){
		if(period.equals("weekly")){
			return 6;
		}else if(period.equals("monthly")){
			return countMonth();
		}else{
			return 0;
		}
	}
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

	public Map<String,String> getTypeAndIPAddress(){
		Map<String, String> result = new HashMap<String,String>();
		InetAddress ip=null;
		try {
			ip = InetAddress.getLocalHost();

			if(ip.getHostAddress().contains("127.0.")){
				result.put("ip", "localhost");
			}else {
				result.put("ip", ip.getHostAddress());
			}
			logger.debug(result.get("ip"));
			logger.debug(result.get("type"));
		} catch (UnknownHostException e) {
			logger.error("Get IP UnknownHostException: "+e.getMessage());
		}


		return result;
	}

	public Map<String, String> getSumNode(String period){
		Map<String,String> node = getTypeAndIPAddress();
		node.putAll(getResultValue(period));
		logger.debug(node.toString());
		return node;
	}
	public Map<String,Object> getCurrentChart(){
		Map<String,String> node = getTypeAndIPAddress();
		Map<String,Object> chart = new HashMap<String, Object>();
		chart.putAll(node);
		chart.put("count",CurrentCountScheduler.getCountList());
		return chart;
	}
	public List<Map<String,String>> getNodeList(){
		List<Map<String,String>> nodeList = new ArrayList<Map<String,String>>();
		return nodeList;
	}

	public Map<String,Object> getReloadChart(String reloadCount){
		Map<String,String> node = getTypeAndIPAddress();
		Map<String,Object> chart = new HashMap<String, Object>();
		chart.putAll(node);
		List<Object> current = new ArrayList<Object>();
		int currentCount = Integer.parseInt(reloadCount);
		for(int i=6-(currentCount/10);i<6;i++){
			current.add(CurrentCountScheduler.getCountList().get(i));
		}
		chart.put("count",current);
		return chart;
	}


	public List<CurrentLogVO> getCurrentLastLog(String ip){
		File file = new File(FilePath.logFile);
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