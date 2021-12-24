package com.terais.avsb.module;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terais.avsb.core.PropertiesData;


/**
  * 기본 설정 파일 생성 클래스
  */
public class DefaultConfigProperties {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConfigProperties.class);

	/**
	  * 기본 설정 값 Properties 생성 메소드
	  * @param file 생성할 Properties 파일
	  */
	public static void createConfigFile(File file) {		
		Properties prop = new Properties();
		try {
			if(!file.exists()){
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				logger.debug("Create properties file");
				file.createNewFile();
				prop.setProperty("use_api", "0");				
				prop.setProperty("port", "12394");
				prop.setProperty("install_path", System.getProperty("user.dir"));
				prop.setProperty("install_day",sdf.format(date));
				prop.setProperty("report_count","30");
				prop.setProperty("date_term", "30");
				prop.setProperty("res_reload_time", "5");
				prop.setProperty("current_reload_time", "10");
				prop.setProperty("log_reload_time", "5");
				prop.setProperty("HTTP", "https://");
				FileOutputStream fos = new FileOutputStream(file);
				prop.store( fos, file.getPath());				
			}
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Create Config File IOException: "+e.getMessage());
		}finally{
			if(file.exists()){
				PropertiesData.callConfig();
//				String date = PropertiesData.installDate;
//				String[] copyDate = date.split("-");
//				FilePath.copyDateLicense = FilePath.getDatePath(copyDate[0],copyDate[1],copyDate[2])+"/.license";
			}
			prop.clear();
		}
	}

	/**
	  * 기본 IP 등록 설정 파일 생성
	  * @param file IP 등록 파일 경로
	  */
	public static void createIpConfigFile(File file) {		
		Properties prop = new Properties();		
		try {
			if(!file.exists()){		
				logger.debug("Create properties file");
				file.createNewFile();
				prop.setProperty("sub_ip", PropertiesData.HTTP+"$"+"127.0.0.1");
				
				FileOutputStream fos = new FileOutputStream(file);
				prop.store( fos, file.getPath());				
			} 
		}catch (IOException e) {
			logger.error("Create IP Config File IOException: "+e.getMessage());
		}finally{
			if(file.exists()){
				PropertiesData.callSubIp();
			}
			prop.clear();
		}
	}

	/**
	  * scheduler.json, report.json 파일에 저장될 JSON 데이터 갱신용 번호를 저장한 Properties 파일 생성
	  */
	public static void createTmpSeqProperties(){
		File file = new File(FilePath.tmpSeq);
		Properties prop = new Properties();
		FileOutputStream fos = null;
		try {
			if(!file.exists()){
				file.createNewFile();
				prop.setProperty("scheduler","0");
				prop.setProperty("report","0");
				fos = new FileOutputStream(file);
				prop.store(fos,file.getCanonicalPath());
			}
		} catch (IOException e) {
			logger.error("Create TmpSeq File IOException: "+e.getMessage());
		}finally{
			if(file.exists()) {
				PropertiesData.callSchedulerSeq();
			}
		}
	}
}
