package com.terais.avsb.module;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;

public class FilePath {	
	
	public static String defaultName = PathAndConvertGson.getOSFolder();
	public static String uploadFolder = defaultName+"/upload";
	public static String uploadExtractFolder = uploadFolder+"/extract";
	public static String extractFolder = defaultName+"/extract";
	public static String uploadFile = "";
	public static String uploadFileSavePath = uploadFolder+"/vaccine_file.tar";
	public static String secondFileSavePath = uploadFolder+"/vaccine_file_0.tar";
	public static String logFolder = defaultName+"/log";
	public static String infoFolder = defaultName+"/info";
	public static String configFolder = defaultName+"/config";
	public static String libsFolder = defaultName+"/libs";
	public static String accountFolder = defaultName+"/account";
	public static String tmpFolder = defaultName+"/tmp";
	public static String log4jLogFolder = logFolder+"/system";
	public static String repoFolder = logFolder+"/repo";
	public static String accountFile = accountFolder+"/login.json";
	public static String accountCountFile = accountFolder+"/login_seq.ini";
	public static String configFile = configFolder+"/config.properties";
	public static String IpConfigFile = configFolder+"/ip_config.properties";
	public static String logPath = "";
	public static String logFile = logPath+"/v3scan_res.log";
	public static String v3File = "";
	public static String readTodayLogFile = null;
	public static String tmpLogJson = logFolder+"/.log_tmp.json";
	public static String workName = infoFolder+"/workers.ini";
	public static String update = infoFolder+"/update_check.properties";
	public static String todayResult = null;
	public static String scheduler = tmpFolder+"/scheduler.json";
	public static String report = tmpFolder+"/report.json";
	public static String tmpLib = tmpFolder+"/lib";
	public static String license = tmpLib+"/.license";
	public static String copyLicense = libsFolder+"/.license";
	public static String copyDateLicense = null;
	public static String tmpSeq = tmpFolder+"/.scheduler_report_seq.properties";
	public static String enginePathFile = libsFolder+"/engine_path.properties";
	public static String fileListPath = tmpFolder+"/file_list.json";
	public static String uploadFileInfo = infoFolder+"/upload_file_info.properties";
	public static String v3properties = (libsFolder+"/vendor/ahnlab/V3Scanner.properties").trim();
	public static String v3option = null;
	public static String copyV3option = null;

	public static String dummyFile = "TERA_SYSTEM_CHECK_DUMMY_FILE";
	
	public static String getDatePath(String year, String month, String day){
		return FilePath.repoFolder+"/"+year+"/"+month+"/"+day;
	}
	
	public static String getDatePath(String year, String month){
		return FilePath.repoFolder+"/"+year+"/"+month;
	}
	
	public static String getDatePath(String year){
		return FilePath.repoFolder+"/"+year;
	}
	
	public static String getReadLogFile(String year, String month, String day){
		return getDatePath(year,month,day)+"/log.json";
	}
	
	public static String getScanResFile(String taskID,String name){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return tmpFolder+"/"+sdf.format(date)+"_"+taskID+"_"+name+".text";
	}

	
}