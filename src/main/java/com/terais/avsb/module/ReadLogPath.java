package com.terais.avsb.module;

import java.io.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.terais.avsb.core.PropertiesData;

public class ReadLogPath {
	
	private static final Logger logger = LoggerFactory.getLogger(ReadLogPath.class);
	
	public static void checkLogPath(File folder, File file){
		try {
			if(!folder.exists()){
				folder.mkdir();
			}
			if(!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void readLogPath(){
		String filePath = FilePath.enginePathFile;
		File file = new File(filePath);
		Properties prop = new Properties();
		FileOutputStream fos = null;
		String path = null;
		try {
			if(!file.exists()){
				file.createNewFile();
				prop.setProperty("engine","NoneEngine");
				prop.setProperty("ahnlab_engine","");
				fos = new FileOutputStream(file);
				prop.store(fos,filePath);
			}
			PropertiesData.callEngineInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("ReadLog Failed: "+e.getMessage());
		}finally {
			PropertiesData.useEngine=PropertiesData.engine;
		}
		path = PropertiesData.enginePath;
		logger.debug("path:" + path);
		File engineFile = new File(path);
		if(!engineFile.exists()){
			path=FilePath.libsFolder+"/log";
			FilePath.logPath=path;
			PropertiesData.isEnginePath=false;
		}else{
			FilePath.logPath=path+"/log";
			FilePath.v3option=PropertiesData.enginePath+"/v3daemon/option.cfg";
			FilePath.copyV3option=FilePath.v3option.replace("option","copy_option");
			FilePath.v3File = path+"/etc/rc.d/v3scan_server";
		}
		logger.debug(FilePath.logPath);
		FilePath.logFile = FilePath.logPath+"/v3scan_res.log";

	}
}