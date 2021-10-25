package com.terais.avsb.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonParseException;
import com.terais.avsb.cron.ScanScheduler;
import com.terais.avsb.dto.ScanResult;
import com.terais.avsb.module.DefaultAccount;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.terais.avsb.dto.ReadLog;
import com.terais.avsb.vo.LoginVO;


public class PathAndConvertGson {

	private static final Logger logger = LoggerFactory.getLogger(PathAndConvertGson.class);

	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	public static <T> List<T> convertGson(String fileName) {
		// TODO Auto-generated method stub
		List<T> list=new ArrayList<T>();

		try {
			File file = new File(fileName);
			if(!file.exists()){
				logger.debug("Not exist File.");
				return list;
			}

			JsonReader reader = new JsonReader(new FileReader(file));

			T[] jsonList=null;
			if(fileName.contains("login.json")){
				jsonList=gson.fromJson(reader, LoginVO[].class);
			}else if(fileName.contains("scheduler.json")||fileName.contains("report.json")){
				jsonList=gson.fromJson(reader, ScanSchedule[].class);
			}else if(fileName.contains("log.text")){
				jsonList=gson.fromJson(reader, ScanResult[].class);
			}else{
				jsonList = gson.fromJson(reader, ReadLog[].class);
				logger.debug("jsonList: "+(jsonList==null));
			}
			if(jsonList!=null){
				for(T json:jsonList){
					list.add(json);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("FileNotFoundException: "+e);
		}catch(JsonParseException e){
			logger.error("Json Parse Exception: "+e.getMessage());
			logger.error("Error Path: "+fileName);
			if(fileName.contains("login.json")){
				list.add((T) DefaultAccount.defaultWrite());
			}else {
				ScanScheduler.saveJsonFile(fileName, list);
			}
		}
		return list;
	}

	public static void deleteFile(File file){
		if(file.exists()){
			file.delete();
		}
	}

	public static String getOsName() {
		// TODO Auto-generated method stub
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("jongtest");
		return os;
	}

	public static String getOSFolder() {
		String os = getOsName();
		String defaultFolder = System.getProperty("user.dir");
		logger.debug("DefaultFolder: "+defaultFolder);
//		defaultFolder="/Users/jong/Desktop/dev/avsb/workspace_avsb1/avsb_base";
		return defaultFolder;
	}

	public static String modListString(String inputString){
		inputString = inputString.replace("[","");
		inputString = inputString.replace("]","");
		return inputString;
	}
}