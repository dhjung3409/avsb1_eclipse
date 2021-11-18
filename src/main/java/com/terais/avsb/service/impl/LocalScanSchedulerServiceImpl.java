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

@Service
public class LocalScanSchedulerServiceImpl implements LocalScanSchedulerService {
	

	private static final Logger logger = LoggerFactory.getLogger(LocalScanSchedulerServiceImpl.class);

	private static final Type type = new TypeToken<List<ScanSchedule>>() {}.getType();

	public Map<String,Object> checkFile(Map<String,String> data){
		logger.debug("insert schedule checkFile: "+data.toString());
		Map<String,Object> checkResult = new HashMap<String, Object>();
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
		String schduleDate = sdf.format(now)+" "+date;
		int result=-1;
		try {
			logger.debug("Comparison Register Date: " + yearSdf.parse(nowTime).after(yearSdf.parse(schduleDate)) + "");
			if (yearSdf.parse(nowTime).after(yearSdf.parse(schduleDate))) {
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

	public int checkFile(String scheduleFilePath,Map<String,String> data,String date){
		logger.debug("insert schedule int checkFile");
		File file = new File(scheduleFilePath);
		FileWriter writer = null;
		try{

			File pathFile = new File(data.get("path"));
			if (pathFile.exists() == false) {
				logger.debug("Not Exist File.");
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
					logger.debug("Can Register 10 Path.");
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
