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


/**
  * JSON 자료를 List로 변환하는 클래스
  */
public class PathAndConvertGson {

	private static final Logger logger = LoggerFactory.getLogger(PathAndConvertGson.class);

	/**
	 * 라이센스 기간 중 연도의 십의 자리 수
	 */
	public static String LI_Y3="2";

	/**
	 * 알아보기 쉽게 배열한 JSON 파일 생성 Gson 객체
	 */
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	  * JSON 파일을 읽어들여 리스트로 전환하는 메소드
	  * @param fileName 읽어들일 JSON 파일
	  * @return JSON 자료가 담긴 리스트
	  */
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

	/**
	  * 파일을 삭제하는 메소드
	  * @param file 삭제할 파일
	  */
	public static void deleteFile(File file){
		if(file.exists()){
			file.delete();
		}
	}

	/**
	  * OS 이름을 가져오는 메소드
	  * @return OS 이름
	  */
	public static String getOsName() {
		// TODO Auto-generated method stub
		String os = System.getProperty("os.name").toLowerCase();
		System.out.println("jongtest");
		return os;
	}

	/**
	  * 기본 폴더를 지정하는 메소드
	  * @return 기본 폴더
	  */
	public static String getOSFolder() {
		String defaultFolder = System.getProperty("user.dir");
		return defaultFolder;
	}

	/**
	  * 입력된 문자열에서 '[', ']' 두가지 기호를 제거하는 메소드
	  * @param inputString 수정할 문자열
	  * @return 기호가 제거된 문자열
	  */
	public static String modListString(String inputString){
		inputString = inputString.replace("[","");
		inputString = inputString.replace("]","");
		return inputString;
	}
}