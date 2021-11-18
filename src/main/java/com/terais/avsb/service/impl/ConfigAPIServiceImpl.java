package com.terais.avsb.service.impl;

import java.io.*;
import java.security.Principal;
import java.util.*;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.cron.SubIPCheckSchduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.ConfigAPIService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Service
public class ConfigAPIServiceImpl implements ConfigAPIService{

	private static final Logger logger = LoggerFactory.getLogger(ConfigAPIServiceImpl.class);

	private String sendFileIP = "";

	public void setPropData(Properties prop,String filePath){
		FileOutputStream fos = null;
		File file = new File(filePath);
		try {
			if (!filePath.contains("ip")) {
				prop.setProperty("date_term", PropertiesData.dateTerm);
				prop.setProperty("use_api", PropertiesData.useApi);
				prop.setProperty("port", PropertiesData.port);
				prop.setProperty("install_path", PropertiesData.installPath);
				prop.setProperty("install_day", PropertiesData.installDate);
				prop.setProperty("report_count",PropertiesData.reportCount);
				prop.setProperty("res_reload_time", PropertiesData.resReloadTime);
				prop.setProperty("current_reload_time", PropertiesData.currentReloadTime);
				prop.setProperty("log_reload_time", PropertiesData.logReloadTime);
				prop.setProperty("HTTP", PropertiesData.HTTP);
			}
			fos=new FileOutputStream(file);
			prop.store(fos, filePath);
		} catch (FileNotFoundException e) {
			logger.error("Set Properties Data FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("Set Properties Data IOException: "+e.getMessage());
		}finally{
			try {
				fos.close();
				prop.clear();
			} catch (IOException e) {
				logger.error("Set Properties Data OutputStream IOException: "+e.getMessage());
			}
		}
	}

	public void setUseAPI(String useAPI) {
		Properties prop = new Properties();
		PropertiesData.useApi=useAPI;
		setPropData(prop, FilePath.configFile);
		logger.debug("useAPI Change");
	}

	public Set<String> setSubIP(String ip) {
		Properties prop = new Properties();		
		PropertiesData.subIp.add(ip);
		String subIp = PropertiesData.subIp.toString();
		subIp=subIp.replace("[", "");
		subIp=subIp.replace("]", "");
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		SubIPCheckSchduler.getRestResult(ip);
		return PropertiesData.subIp;
	}

	public Set<String> delSubIP(List<String> delItems){
		Properties prop = new Properties();		
		Set<String> subIpList = PropertiesData.subIp;
		int defaultIp = delItems.indexOf("127.0.0.1");

		if(defaultIp!=-1){
			logger.error("IP 127.0.0.1 can not delete");
			delItems.remove(defaultIp);
		}

		for(String delItem : delItems){
			subIpList.remove(delItem);
		}
		String subIp = subIpList.toString();
		PropertiesData.subIp=subIpList;
		subIp=PathAndConvertGson.modListString(subIp);
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		return subIpList;
	}



	public void setDateTerm(String rotate) {
		Properties prop = new Properties();
		PropertiesData.dateTerm=rotate;
		setPropData(prop, FilePath.configFile);		
	}
	
	public void setReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.resReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}
	
	public void setCurrentReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.currentReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}

	public void setReportCount(String count){
		Properties prop = new Properties();
		PropertiesData.reportCount=count;
		setPropData(prop,FilePath.configFile);
	}
	public void setLogReloadTime(String reload){
		Properties prop = new Properties();
		PropertiesData.logReloadTime=reload;
		setPropData(prop,FilePath.configFile);
	}

}
