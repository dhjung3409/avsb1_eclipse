package com.terais.avsb.module;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terais.avsb.core.PropertiesData;


public class DefaultConfigProperties {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAccount.class);

	public static void createConfigFile(File file) {		
		Properties prop = new Properties();
		try {
			if(!file.exists()){
				Date date = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

				logger.debug("Create properties file");
				file.createNewFile();
				prop.setProperty("use_api", "0");				
				prop.setProperty("port", "8081");
				prop.setProperty("install_path", System.getProperty("user.dir"));
				prop.setProperty("install_day",sdf.format(date));
				prop.setProperty("report_count","30");
				prop.setProperty("date_term", "30");
				prop.setProperty("res_reload_time", "5");
				prop.setProperty("current_reload_time", "10");
				prop.setProperty("log_reload_time", "5");
				FileOutputStream fos = new FileOutputStream(file);
				prop.store( fos, file.getPath());				
			}
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Create Config File IOException: "+e.getMessage());
		}finally{
			if(file.exists()){
				PropertiesData.callConfig();
				String date = PropertiesData.installDate;
				String[] copyDate = date.split("-");
				FilePath.copyDateLicense = FilePath.getDatePath(copyDate[0],copyDate[1],copyDate[2])+"/.license";
			}
			prop.clear();
		}
	}

	public static void createIpConfigFile(File file) {		
		Properties prop = new Properties();		
		try {
			if(!file.exists()){		
				logger.debug("Create properties file");
				file.createNewFile();
				prop.setProperty("sub_ip", "127.0.0.1");
				
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

	public static void updateFileCheck(){
		String path = FilePath.update;
		Properties prop = new Properties();
		File file = new File(path);
		FileInputStream fis =null;
		FileOutputStream fos = null;
		String date = null;
		try {
			fos = new FileOutputStream(file);
			if(!file.exists()){
				prop.setProperty("update","");
				prop.store(fos,path);
			}
			fis = new FileInputStream(file);
			prop.load(fis);
			date = prop.getProperty("update");
			if(date!=null&&!date.equals("")){
				PropertiesData.updateDate=date;
			}
		} catch (FileNotFoundException e) {
			logger.error("Update Data FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("Update Data IOException: "+e.getMessage());
		}finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch(IOException e) {
				logger.error("Update Data FileOutputStream: "+e.getMessage());
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch(IOException e) {
				logger.error("Update Data FileInputStream: "+e.getMessage());
			}
		}
	}

	public static void getUploadFileInfo(){
		Properties prop = PropertiesData.getProp(FilePath.uploadFileInfo);
		if(!prop.isEmpty()) {
			String[] fileNames = prop.getProperty("file_name").split(",");
			for (String fileName : fileNames) {
				PropertiesData.updateFileNames.add(fileName);
			}
			String[] fileSize = prop.getProperty("file_size").split(",");
			for (String size : fileSize) {
//				logger.info("size: "+size);
//				logger.info("boolean size: "+(size==null));
				if(size==null|| size.contains("null")||size.length()==0){

				}else {
					long sizeToLong = Long.parseLong(size.trim());
					PropertiesData.updateFileSize.add(sizeToLong);
				}
			}
//			logger.info("size toString: "+PropertiesData.updateFileSize.toString());
			String[] fileDate = prop.getProperty("file_date").split(",");
			for (String date : fileDate) {
				PropertiesData.updateFileDate.add(date);
			}
		}else{
			File file = new File(FilePath.uploadFileInfo);
			if(!file.exists()){
				try {
					file.createNewFile();
				} catch (IOException e) {
					logger.error("create upload info file IOException: "+e.getMessage());
				}
			}
		}
	}
}
