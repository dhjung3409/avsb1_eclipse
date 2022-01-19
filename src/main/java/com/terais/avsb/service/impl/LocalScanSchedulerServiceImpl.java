package com.terais.avsb.service.impl;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.ScanScheduleList;
import com.terais.avsb.core.SimpleDateFormatCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.LocalScanSchedulerService;
import com.terais.avsb.vo.ScanSchedule;

/**
  * 스케줄러 등록 데이터 등록, 삭제 그리고 스케줄러 결과 리포트 데이터 삭제 기능을 지니는 클래스
  */
@Service
public class LocalScanSchedulerServiceImpl implements LocalScanSchedulerService {
	

	private static final Logger logger = LoggerFactory.getLogger(LocalScanSchedulerServiceImpl.class);

	/**
	 * Gson 객체에서 사용 할 List<ScanSchedule> TypeToken
	 */
	private static final Type type = new TypeToken<List<ScanSchedule>>() {}.getType();

	/**
	   스케줄러 검사 등록 데이터가 들어왔을 때  데이터에 포함된 해당 경로에 문제가 있는지 확인하고 등록하는 메소드
	   @param data 스케줄에 등록하려는 데이터
	   @return checkResult 스케줄러 등록 결과
	*/
	public Map<String,Object> checkFile(Map<String,String> data){
		logger.debug("insert schedule checkFile: "+data.toString());
		Map<String,Object> checkResult = new HashMap<String, Object>();
		if(data.get("path").lastIndexOf("/")==(data.get("path").length()-1)||data.get("path").indexOf("//")!=-1){
			logger.info("inputFullPath: "+data.get("path"));
			String[] splitPath = data.get("path").split("/");
			String fullPath = "";
			for(String split:splitPath){
				if(split.equals("")){
					continue;
				}
				fullPath=fullPath+"/"+split;
				logger.info("fullPath: "+fullPath);
			}
			data.put("path",fullPath);
		}
		File file = new File(data.get("path"));
		if(!file.exists()){
			checkResult.put("message",2);
			checkResult.put("result",false);
		}else if(!file.canRead()){
			checkResult.put("message",3);
			checkResult.put("result",false);
		}else{
			logger.debug("insert schedule checkFile else");
			checkResult=partSelect(data);
		}

		return checkResult;
	}

	/**
	  * 스케줄러 등록 데이터를 Type 종류에 따라 분류해서 등록 검사를 진행시키는 메소드
	  * @param data 스케줄에 등록하려는 데이터
	  * @return 스케줄러  등록 결과
	  */
	public Map<String, Object> partSelect(Map<String,String> data){
		int result= -1;
		if(data.get("type").equals("once")){
			logger.debug("once");
			result = registerScheduler(data);
		}else{
			logger.debug("repeat");
			result = registerRepeatScheduler(data);
		}
		logger.debug("part Select: "+result);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(result==0){
			resultMap.put("result",true);
		}else{
			resultMap.put("result",false);
		}
		resultMap.put("message",result);
		logger.debug(resultMap.toString());
		return resultMap;
	}

	/**
	  * 스케줄러 등록 데이터 중 Type 키가 once 값인 경우 시간 비교로 등록 여부 검사를 진행하는 메소드
	  * @param data 스케줄에 등록하려는 데이터
	  * @return result 스케줄러  등록 결과
	  */
	public int registerScheduler(Map<String,String> data){
		logger.debug("registScheduler start");
		String scheduleFilePath = FilePath.scheduler;
		String date = data.get("year")+"/"+data.get("period")+" "+data.get("hour")+":"+data.get("min");
		int result = -1;
		logger.debug(date);
		SimpleDateFormat sdf = SimpleDateFormatCore.sdf;
		Date now = new Date();
		String nowTime = sdf.format(now);
		logger.debug(nowTime);

		try {
			logger.debug("nowTime: "+nowTime);
			logger.debug("Comparison Register Date: "+sdf.parse(nowTime).after(sdf.parse(date))+"");
			logger.debug("compareTo: "+sdf.parse(nowTime).compareTo(sdf.parse(date))+"");
			if(sdf.parse(nowTime).compareTo(sdf.parse(date))<0){
				result = checkFile(scheduleFilePath,data,date);
				logger.debug("register result;"+result);
			}else{
				logger.debug("Can Not Register Time.");
				return 1;
			}
		} catch (ParseException e) {
			logger.error("RegistScheduler ParseException: "+e.getMessage());
		}
		return result;
	}

	/**
	  * 스케줄러 등록 데이터 중 Type 키가 repeat 값인 경우 시간 비교로 등록 여부 검사를 진행하는 메소드
	  * @param data 스케줄에 등록하려는 데이터
	  * @return 스케줄러  등록 결과
	  */
	public int registerRepeatScheduler(Map<String,String> data){
		logger.debug("registerRepeatScheduler start");
		logger.debug("data: "+data.toString());
		String scheduleFilePath = FilePath.scheduler;
		String date = data.get("hour")+":"+data.get("min");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat yearSdf = SimpleDateFormatCore.sdf;
		Calendar cl = Calendar.getInstance();
		Date now = new Date();
		cl.setTime(now);
		String nowTime = yearSdf.format(now);
		String scheduleDate = sdf.format(now)+" "+date;
		int result=-1;
		try {
			logger.debug("Comparison Register Date: " + yearSdf.parse(nowTime).after(yearSdf.parse(scheduleDate)) + "");
			if (yearSdf.parse(nowTime).after(yearSdf.parse(scheduleDate))) {
				logger.debug("Register Day Exception Scan Date.");
				if(data.get("period").equals("weekly")){
					cl.add(Calendar.DATE,7);
				}else if(data.get("period").equals("monthly")){
					cl.add(Calendar.MONTH,1);
				}else{
					cl.add(Calendar.DATE,1);
				}
				date = sdf.format(cl.getTime())+" "+data.get("hour")+":"+data.get("min");

			} else {
				date=sdf.format(now)+" "+data.get("hour")+":"+data.get("min");

			}

			result = checkFile(scheduleFilePath,data,date);
			logger.debug("repeatRegister result;"+result);
		}catch(ParseException e){
			logger.error("registerRepeatScheduler ParseException: "+e.getMessage());
		}
		return result;
	}

	/**
	  * 스케줄러 등록 데이터에 시간과 경로가 이미 등록되어있는지 확인하는 메소드
	  * @param path 등록 경로
	  * @param date 등록 시간
	  * @param list - 이미 등록된 스케줄 리스트
	  * @return 이미 등록된 스케줄과의 중복 여부 | 5 : 이미 등록된 스케줄 | 6 : 상위 경로로 이미 등록된 스케줄 | -1 : 스케줄러에 중복 경로 없음
	  */
	public int checkOverlap(String path, String date,List<ScanSchedule> list){
		int result =-1;
		try {
			Date registDate = SimpleDateFormatCore.sdf.parse(date);
			logger.debug("add Date: "+date);
			for(ScanSchedule scan : list){
				logger.debug("list Date: "+scan.getReservationDate());
				int timeResult = SimpleDateFormatCore.sdf.parse(scan.getReservationDate()).compareTo(registDate);
				if(scan.getPath().equals(path)&&timeResult==0){
					logger.debug("Register Schedule");
					result=5;
					return result;
				}else if(timeResult==0){
					logger.debug("Same Time");
					int rootLocate = path.indexOf(scan.getPath());
					if(rootLocate==0){
						logger.debug("Register upper Schedule");
						result=6;
						return result;
					}
				}
			}
		} catch (ParseException e) {
			logger.error("CheckOverlap ParseException: "+e.getMessage());
		}
		return -1;
	}

	/**
	  * 스케줄러 등록 시 문제되는 점은 없는지 확인하고 문제 없는 경우 스케줄러에 등록시키는 클래스
	  * @param scheduleFilePath 스케줄 데이터 파일 경로
	  * @param data 스케줄에 등록 할 데이터
	  * @param date 스케줄 가동 시간
	  * @return Num 등록 확인 결과
	  */
	public int checkFile(String scheduleFilePath,Map<String,String> data,String date){
		logger.debug("insert schedule int checkFile");
		File file = new File(scheduleFilePath);
		FileWriter writer = null;
		try{

			File pathFile = new File(data.get("path"));
			if (pathFile.exists() == false) {
				logger.debug("Not Exists File.");
				return 2;
			} else if (pathFile.canRead() == false) {
				logger.debug("Can Not Read This File.");
				return 3;
			} else {


				int overlapResult = checkOverlap(data.get("path"),date, ScanScheduleList.scanSchedule);
				if(overlapResult!=-1){
					logger.debug("checkFile result: "+overlapResult);
					return overlapResult;
				}

				if (ScanScheduleList.scanSchedule.size() >= 12) {
					logger.debug("Can Register 12 Path.");
					return 4;
				}

				logger.debug("size Check");
				ScanSchedule scanSchedule = setScanSchedule(data, PropertiesData.schedulerSeq++,date);
				logger.debug("set Schedule");
				ScanScheduleList.scanSchedule.add(scanSchedule);
				logger.debug("list Add");
				Collections.sort(ScanScheduleList.scanSchedule, new ScanSchedule());
				logger.debug("scanSchedule: {}", ScanScheduleList.scanSchedule);
				PropertiesData.setScheduleReportSeq("scheduler");
				writer = new FileWriter(file);
				PathAndConvertGson.gson.toJson(ScanScheduleList.scanSchedule, writer);
				writer.flush();
				logger.debug("Register Scheduler - Date: "+scanSchedule.getReservationDate()+", Path: "+scanSchedule.getPath()+", TaskID: "+scanSchedule.getTaskID());
			}
		} catch (IOException e) {
			logger.error("File Check IOException: "+e.getMessage());
		} finally{
			try {
				if(writer!=null) {
					writer.close();
				}
			} catch (IOException e) {
				logger.error("File Check Writer IOException: "+e.getMessage());
			}
		}
		return 0;
	}

	/**
	  * ScanSchedule 객체에 스케줄 데이터를 저장하는 메소드
	  * @param data 객체에 저장 할 스케줄 데이터
	  * @param no 스케줄 등록 번호
	  * @param date 스케줄 작동 시기
	  * @return 데이터를 저장한 ScanSchedule 객체
	  */
	public ScanSchedule setScanSchedule(Map<String,String> data,int no,String date){
		logger.debug(date);
		ScanSchedule scanSchedule = new ScanSchedule();
		if(data.get("type").equals("repeat")){
			scanSchedule.setCycle(data.get("period"));
		}
		scanSchedule.setNo(no);
		scanSchedule.setPath(data.get("path"));
		scanSchedule.setReport("");
		scanSchedule.setResult("wait");
		scanSchedule.setReservationDate(date);
		scanSchedule.setTaskID(data.get("time"));
		scanSchedule.setType(data.get("type"));
		return scanSchedule;
	}

	/**
	  * 등록된 스케줄러, 스케줄러 결과 리포트를 삭제하는 메소드
	  * @param no 제거할 스케줄러 고유 번호
	  * @param path 스케줄러 혹은 결과 리포트 데이터가 저장된 파일 경로
	  * @param ss 스케줄러 혹은 결과 리포트 데이터 리스트
	  */
	public void deleteScanScheduler(List<String> no,String path, List<ScanSchedule> ss){
		logger.debug("no: "+no.toString());
		String deleteInfo = "";
		logger.debug("no: "+ss.toString());
		for(int i=0;i<ss.size();i++){
			logger.debug("no: "+ss.get(i).toString());
			ScanSchedule scan = ss.get(i);
			if(no.contains(String.valueOf(scan.getNo()))){
				if(path.equals(FilePath.report)){
					deleteResultFile(scan.getReport());
				}
				deleteInfo = "Date: "+scan.getReservationDate()+", Path: "+scan.getPath()+", TaskID: "+scan.getTaskID();
				ss.remove(ss.get(i--));
				logger.debug("Delete Scheduler - "+deleteInfo);
			}
		}
		logger.debug("ss: "+ss.toString());
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			PathAndConvertGson.gson.toJson(ss,fw);
			fw.flush();
			logger.debug("Scheduler Element Delete");
		} catch (IOException e) {
			logger.error("Delete Scheduler IOException: "+e.getMessage());
		}finally{

			try {
				if(fw!=null) {
					fw.close();
				}
			} catch (IOException e) {
				logger.error("Delete Scheduler Writer IOException: "+e.getMessage());
			}
		}

	}

	/**
	  * 스케줄러 결과 리포트 관련 파일 제거 메소드
	  * @param fileName - 제거할 log.txt 파일 경로
	  */
	public void deleteResultFile(String fileName){
		List<String> filePaths = new ArrayList<String>();
		filePaths.add(FilePath.tmpFolder+"/"+fileName);
		filePaths.add(FilePath.tmpFolder+"/"+fileName.replace("log","report"));

		for(String filePath:filePaths) {
			File file = new File(filePath);
			PathAndConvertGson.deleteFile(file);
			try {
				logger.debug(file.getCanonicalPath()+": delete");
			} catch (IOException e) {
				logger.error("Delete File["+file.getPath()+"] IOException Failed: "+e.getMessage());
			}
		}

	}
}
