package com.terais.avsb.module;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
  * 로그 경로를 읽어오는 클래스
  */
public class ReadLogPath {

	private static final Logger logger = LoggerFactory.getLogger(ReadLogPath.class);
	
	/**
	  * 로그파일을 감지하지 못했을 때 임시 로그 파일을 생성하는 메소드
	  * @param folder 임시 로그 폴더
	  * @param file 임시 로그 파일
	  */
	public static void checkLogPath(File folder, File file){
		try {
//			folder.
			if(!folder.exists()){
				folder.mkdir();
			}
			if(!file.exists()){
				file.createNewFile();
			}
		} catch (IOException e) {
			logger.error("Failed path config");
		}
	}
	
	/**
	  * 로그 관련 경로를 세팅하는 메소드
	  */
	public static void readLogPath(){
		String filePath = FilePath.enginePathFile;
		File file = new File(filePath);
		Properties prop = new Properties();
		FileOutputStream fos = null;
		String path = null;
		try {
			if(!file.exists()){
				file.createNewFile();
				prop.setProperty("engine","0");
				prop.setProperty("ahnlab_engine","");
				prop.setProperty("hauri_engine","");
				prop.setProperty("alyac_engine","");
				prop.setProperty("tachyon_engine","");
				fos = new FileOutputStream(file);
				prop.store(fos,filePath);
			}
			PropertiesData.callEngineInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("ReadLog Failed: "+e.getMessage());
		}finally {
			int engineNum = PropertiesData.engine;
			if(engineNum==1){
				PropertiesData.useEngine="TSEngine";
			}else if(engineNum==2){
				PropertiesData.useEngine="VrSDK";
			}else if(engineNum==3){
				PropertiesData.useEngine="Alyac";
			}else if(engineNum==4){
				PropertiesData.useEngine="Tachyon";
			}else{
				PropertiesData.useEngine="None";
			}

		}
		path = PropertiesData.enginePath;
		File fileName = new File(path);
		logger.debug("path:" + path);
		File engineFile = new File(path);
		logger.info("dummy logFile : "+path.contains(FilePath.libsFolder));
		if(!engineFile.exists()||(path.contains(FilePath.libsFolder))){
			path=FilePath.libsFolder+"/log";
			FilePath.logPath=path;
			PropertiesData.isEnginePath=false;
		}else if(PropertiesData.engine==1){
			FilePath.logPath=fileName.getParent();
			FilePath.v3option=PropertiesData.enginePath+"/v3daemon/option.cfg";
			FilePath.copyV3option=FilePath.v3option.replace("option","copy_option");
			FilePath.v3File = path+"/etc/rc.d/v3scan_server";
		}else if(PropertiesData.engine==2){
			FilePath.logPath=fileName.getParent();
			File logDir = new File(FilePath.logPath);
			if(logDir.exists()==false){
				logDir.mkdirs();
			}else{

			}
			FilePath.directoryFileList = FilePath.logPath+"/fileList.txt";
		}
		logger.debug(FilePath.logPath);
		FilePath.logFile = path;

	}
}