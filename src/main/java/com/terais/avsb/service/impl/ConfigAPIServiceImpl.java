package com.terais.avsb.service.impl;

import java.io.*;
import java.security.Principal;
import java.util.*;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.cron.SubIPCheckScheduler;
import com.terais.avsb.module.IpListSortByIp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.ConfigAPIService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


/**
  * 설정 파일에 저장 할 데이터를 다루는 클래스
  */
@Service
public class ConfigAPIServiceImpl implements ConfigAPIService{

	private static final Logger logger = LoggerFactory.getLogger(ConfigAPIServiceImpl.class);

	/**
	  * PropertiesData 클래스에 저장된 Properties 데이터를 파일에 입력시키는 클래스
	  * @param prop Properties 데이터를 담아놓을 객체
	  * @param filePath 저장될 파일의 경로
	  */
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

	/**
	  * useAPI 항목을 저장하는 메소드
	  * @param useAPI 저장할 userAPI 데이터
	  */
	public void setUseAPI(String useAPI) {
		Properties prop = new Properties();
		PropertiesData.useApi=useAPI;
		setPropData(prop, FilePath.configFile);
		logger.debug("useAPI Change");
	}

	/**
	  * 등록된 IP 목록 정보를 가져오는 메소드
	  * @return 등록된 IP 목록
	  */
	public List<String[]> getSubIP(){
		List<String[]> httpIP = new ArrayList<String[]>();
		List<String> sortIP = new ArrayList<String>();
		for(String ip : PropertiesData.subIp){
			sortIP.add(ip);
		}

		Collections.sort(sortIP, new IpListSortByIp());

		for(String ip : sortIP){
			String[] ipInfo = ip.split("\\$");
			httpIP.add(ipInfo);
		}

		return httpIP;
	}

	/**
	  * IP 정보를 등록하는 메소드
	  * @param ip - String - 등록할 IP 정보
	  * @return 등록된 IP 목록
	  */
	public Set<String> setSubIP(String ip) {
		logger.info("Input IP: "+ip);
		Properties prop = new Properties();		
		PropertiesData.subIp.add(ip);
		String subIp = PropertiesData.subIp.toString();
		subIp=subIp.replace("[", "");
		subIp=subIp.replace("]", "");
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		SubIPCheckScheduler.getRestResult(ip);
		return PropertiesData.subIp;
	}

	/**
	  * 등록된 IP를 제거하는 메소드
	  * @param delItems 제거할 IP 목록
	  * @return subIpList IP가 제거된 이후 남은 등록 IP 목록
	  */
	public Set<String> delSubIP(List<String> delItems){
		logger.info("delItems: "+delItems);
		Properties prop = new Properties();		
		Set<String> subIpList = PropertiesData.subIp;
		int defaultIp = delItems.indexOf("127.0.0.1");

		if(defaultIp!=-1){
			logger.error("IP 127.0.0.1 can not delete");
			delItems.remove(defaultIp);
		}

		for(String delItem : delItems){
			for(String ips:subIpList){
				if(ips.contains(delItem)){
					delItem=ips;
					break;
				}
			}
			subIpList.remove(delItem);
		}
		String subIp = subIpList.toString();
		PropertiesData.subIp=subIpList;
		subIp=PathAndConvertGson.modListString(subIp);
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		return subIpList;
	}

	public void setServerPort(String port){
		logger.info("port: "+port);
	}

	/**
	  * 로그 데이터 저장 기간을 설정하는 메소드
	  * @param rotate 로그 데이터 저장 기간
	  */
	public void setDateTerm(String rotate) {
		Properties prop = new Properties();
		PropertiesData.dateTerm=rotate;
		setPropData(prop, FilePath.configFile);		
	}
	
	/**
	  * 악성코드 감염 현황 갱신 주기를 설정하는 메소드
	  * @param reload 악성코드 감염 현황 갱신 주기
	  */
	public void setReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.resReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}
	
	/**
	  * 실시간 검사 현황 표시 갱신 주기 설정 메소드
	  * @param reload 실시간 검사 현황 표시 갱신 주기
	  */
	public void setCurrentReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.currentReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}

	/**
	  * 저장 가능한 리포트의 개수를 설정하는 메소드
	  * @param count 저장 가능한 리포트의 개수
	  */
	public void setReportCount(String count){
		Properties prop = new Properties();
		PropertiesData.reportCount=count;
		setPropData(prop,FilePath.configFile);
	}
	
	/**
	  * 실시간 검사 로그 표시 갱신 주기 설정 메소드
	  * @param reload 실시간 검사 로그 표시 갱신 주기
	  */
	public void setLogReloadTime(String reload){
		Properties prop = new Properties();
		PropertiesData.logReloadTime=reload;
		setPropData(prop,FilePath.configFile);
	}

}
