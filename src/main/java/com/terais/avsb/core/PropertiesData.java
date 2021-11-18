package com.terais.avsb.core;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.cron.LogReadScheduler;
import com.terais.avsb.cron.ScanScheduler;
import com.terais.avsb.cron.SubIPCheckSchduler;
import com.terais.avsb.module.DefaultAccount;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.util.Password;


public class PropertiesData {

	private static final Logger logger = LoggerFactory.getLogger(PathAndConvertGson.class);

	public static String useApi = null;
	public static String installPath = null;
	public static String installDate = null;
	public static String reportCount = null;
	public static Set<String> subIp = new HashSet<String>();
	public static Map<String,Boolean> ipConnect =new HashMap<String, Boolean>();
	public static String port = null;
	public static String worker = null;
	public static String logStatus = null;
	public static long accountCount = -1;
	public static String dateTerm = null;
	public static String resReloadTime = null;
	public static String currentReloadTime = null;
	public static String logReloadTime = null;
	public static String useEngine = "NoneEngine";
	public static boolean licenseStatus = false;
	public static boolean licenseMonthStatus = true;
	public static int licenseRemain = -1;
	public static int schedulerSeq = 0;
	public static int reportSeq = 0;
	public static String engine = null;
	public static String enginePath = null;
	public static boolean isEnginePath=true;
	public static String licenseExpire = PasswordAlgorithm.LI_Y1+ SubIPCheckSchduler.LI_Y2+PathAndConvertGson.LI_Y3+CurrentLog.LI_Y4+FilePath.LI_DA+ ScanScheduler.LI_M1+ LogReadScheduler.LI_M2+FilePath.LI_DA+ CurrentCountScheduler.LI_D1+ScanScheduleList.LI_D2;
	public static String osName = null;
	public static String HTTP = "http://";
	
	public static void callConfig(){
		String configFile = FilePath.configFile;
		Properties prop = getProp(configFile);
		try {
			useApi = prop.get("use_api").toString();
			port = prop.get("port").toString();
			installPath = prop.get("install_path").toString();
			installDate = prop.get("install_day").toString();
			dateTerm = prop.get("date_term").toString();
			resReloadTime = prop.get("res_reload_time").toString();
			currentReloadTime = prop.get("current_reload_time").toString();
			logReloadTime = prop.get("log_reload_time").toString();
			reportCount = prop.get("report_count").toString();
			HTTP=prop.get("HTTP").toString();
		}catch(NullPointerException e){
			logger.error("Call Config NullPointerException: "+e.getMessage());
			setErrorConfig(prop,configFile);
		}catch(Exception e){
			logger.error("Call Config Error: "+e.getMessage());
			setErrorConfig(prop,configFile);
		}
		prop.clear();
		
	}
	public static void setErrorConfig(Properties prop,String path){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		useApi = prop.get("usr_api")==null?setValue(prop,"usr_api","0"):prop.getProperty("usr_api");
		port = prop.get("port")==null?setValue(prop,"port","12394"):prop.getProperty("port");
		installPath = prop.get("install_path")==null?setValue(prop,"install_path",System.getProperty("user.dir")):prop.getProperty("install_path");
		installDate = prop.get("install_day")==null?setValue(prop,"install_day",sdf.format(date)):prop.getProperty("install_day");
		dateTerm = prop.get("date_term")==null?setValue(prop,"date_term","30"):prop.getProperty("date_term");
		resReloadTime = prop.get("res_reload_time")==null?setValue(prop,"res_reload_time","5"):prop.getProperty("res_reload_time");
		currentReloadTime = prop.get("current_reload_time")==null?setValue(prop,"current_reload_time","10"):prop.getProperty("current_reload_time");
		logReloadTime=prop.get("log_reload_time")==null?setValue(prop,"log_reload_time","5"):prop.getProperty("log_reload_time");
		reportCount = prop.get("report_count")==null?setValue(prop,"report_count","30"):prop.getProperty("report_count");
		HTTP = prop.get("HTTP")==null?setValue(prop,"HTTP","https://"):prop.getProperty("HTTP");

		setProp(prop,path);

	}
	public static String setValue(Properties prop,String key,String value){
		prop.setProperty(key,value);
		return value;

	}
	
	public static void callSubIp(){
		String ipConfigFile = FilePath.IpConfigFile;		
		Properties prop = getProp(ipConfigFile);
		try {
			String ips = prop.get("sub_ip").toString();
			for (String ip : ips.split(",")) {
				logger.debug("Add Sub IP: "+ip);
				if (!ip.equals("")&&RegularExpression.checkIP(ip.substring(ip.indexOf("$")+1).trim())) {
					subIp.add(ip.trim());
				}
			}
		}catch(NullPointerException e){
			logger.error("Call SubIP NullPointerException: "+e.getMessage());
			if(subIp.size()==0){
				subIp.add(HTTP+"/127.0.0.1");
			}
		}catch (Exception e){
			logger.error("Call SubIP Error: "+e.getMessage());
			if(subIp.size()==0){
				subIp.add(HTTP+"/127.0.0.1");
			}
		}
		prop.clear();
	}
	
	public void callWorker(){
		String workerFile = FilePath.workName;
		Properties prop = getProp(workerFile);
		try {
			worker = prop.get("worker").toString();
			logStatus = prop.get("status").toString();
		}catch (NullPointerException e){
			logger.error("Call Worker NullPointerException: "+e.getMessage());
			worker="0";
			logStatus = "Initialization: normal";
		}catch (Exception e){
			logger.error("Call Worker Error: "+e.getMessage());
			worker="0";
			logStatus = "Initialization: normal";
		}
		prop.clear();
	}

	public static void callSchedulerSeq(){
		Properties prop = getProp(FilePath.tmpSeq);
		try {
			schedulerSeq = Integer.parseInt(prop.get("scheduler").toString());
			reportSeq = Integer.parseInt(prop.get("report").toString());
			logger.debug("reportSeq: "+reportSeq);
		}catch(NullPointerException e){
			logger.error("Call Scheduler Seq NullPointerException: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}catch(NumberFormatException e){
			logger.error("Call Scheduler Seq NumberFormatException: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}catch (Exception e){
			logger.error("Call Scheduler Seq Error: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}
		prop.clear();
	}

	public static void setErrorSchedule(String type){
		List<ScanSchedule> ss = null;
		if(type.equals("scheduler")) {
			ss = PathAndConvertGson.convertGson(FilePath.scheduler);
		}else {
			ss = PathAndConvertGson.convertGson(FilePath.report);
		}
		int no = 0;
		for(ScanSchedule scan : ss){
			if(scan.getNo()>no){
				no= (int) scan.getNo();
			}
		}
		if(type.equals("scheduler")){
			schedulerSeq= no;
		}else{
			reportSeq=no;
		}
	}

	public static void setScheduleReportSeq(String name){
		Properties prop = getProp(FilePath.tmpSeq);

		if(name.equals("scheduler")){
			logger.debug("scheduler: "+PropertiesData.schedulerSeq);
			prop.setProperty("scheduler", String.valueOf(PropertiesData.schedulerSeq));
		}else{
			logger.debug("report: "+PropertiesData.reportSeq);
			prop.setProperty("report", String.valueOf(++PropertiesData.reportSeq));
		}
		setProp(prop,FilePath.tmpSeq);
	}
	
	public static void callAccountSeq(){
		String accountSeq = FilePath.accountCountFile;
		Properties prop = getProp(accountSeq);
		try {
			accountCount = Long.parseLong(prop.get("no").toString());
		}catch (NullPointerException e){
			logger.error("Call Account Seq NullPointerException: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}catch (NumberFormatException e){
			logger.error("Call Account Seq NumberFormatException: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}catch (Exception e){
			logger.error("Call Account Seq Error: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}
		prop.clear();
	}

	public static void callEngineInfo(){
		String engineFile = FilePath.enginePathFile;
		Properties prop = getProp(engineFile);
		try {
			enginePath = prop.get("ahnlab_engine").toString();
			File file = new File(enginePath);
			if(file.exists()) {
				engine = "TSEngine";
			}else{
				engine="NoneEngine";
				enginePath = "";
			}
		}catch(NullPointerException e){
			logger.error("Call Engine Info NullPointerException: "+e.getMessage());
			engine = "NoneEngine";
			enginePath = "";
		}catch(Exception e){
			logger.error("Call Engine Info Error: "+e.getMessage());
			engine = "NoneEngine";
			enginePath = "";
		}
		prop.clear();
	}

	public static Properties getProp(String filePath){
		Properties pro = new Properties();
		FileInputStream fis = null;
		try {
			fis=new FileInputStream(filePath);
			pro.load(fis);
		} catch (FileNotFoundException e) {
			logger.error("getProperties FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("getProperties IOException: "+e.getMessage());
		}finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error("Get Properties FileInputStream IOException: "+e.getMessage());
			}
		}

		return pro;
	}

	public static void setProp(Properties pro, String path){
		File file = new File(path);
		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(file);
			pro.store(fos,path);

		} catch (FileNotFoundException e) {
			logger.error("setProperties FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("setProperties IOException: "+e.getMessage());
		}finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("Set Properties FileOutputStream IOException: "+e.getMessage());
			}
		}
	}
	
}
