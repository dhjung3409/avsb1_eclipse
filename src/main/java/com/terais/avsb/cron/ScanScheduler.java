package com.terais.avsb.cron;

import com.ahnlab.v3engine.V3Const;
import com.ahnlab.v3engine.V3Scanner;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.ScanScheduleList;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.dto.ScanResult;
import com.terais.avsb.lib.ViRobotLog;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
  * 일정 주기마다 스캔 동작을 실행하는 스케줄러를 가지고 있는 클래스
  */
@Component
public class ScanScheduler {

	private static final Logger logger = LoggerFactory.getLogger(ScanScheduler.class);

	/**
	 * 검사할 디렉토리가 지니고 있는 파일 목록의 절대경로를 가지고 있는 객체
	 */
	private static Map<Long,String> fileList = new HashMap<Long, String>();

	/**
	 * 라이센스 기간 중 월의 십의 자리 수
	 */
	public static String LI_M1="0";

	/**
	 * 검사 결과 값들을 카운팅해 담아둔 맵 객체
	 */
	private static Map<String,Integer> resCount = new HashMap<String, Integer>();

	private static Map<String,Integer> resultMap = new HashMap<String, Integer>();

	public static void initResultMap(){
		int engine = PropertiesData.engine;
		if(engine==1){
			resultMap.put("normal",0);
			resultMap.put("infected",1);
			resultMap.put("disinfected",2);
			resultMap.put("delete",4);

		}else if(engine==2){
			resultMap.put("normal",2);
			resultMap.put("infected",3);
			resultMap.put("disinfected",2);
			resultMap.put("delete",4);
		}else{

		}
	}

	/**
	  * resCount 초기화 메소드
	  */
	public void initResCount(){
		resCount.put("Total",0);
		resCount.put("Normal",0);
		resCount.put("Infected",0);
		resCount.put("Cured",0);
		resCount.put("Failed",0);
	}

	/**
	  * file_list.json에 저장되어있는 fileList 파일들에 대한 정보를 fileList에 적용시키는 메소드
	  */
	public void initFileList(){
		if(fileList.size()>0){
			return;
		}
		File file = new File(FilePath.fileListPath);
		if(!file.exists()){
			return;
		}
		BufferedReader bfr = null;
		FileReader fr = null;
		String line;
		String[] path;
		try {
			fr=new FileReader(file);
			bfr = new BufferedReader(fr);
			while((line=bfr.readLine())!=null){
				path=line.split(",");
				fileList.put(Long.valueOf(path[0]),path[1]);
			}
		} catch (FileNotFoundException e) {
			logger.error("Init FileList FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("Init FileList IOException: "+e.getMessage());
		}finally {
			fr=null;
			bfr=null;
		}
	}

	/**
	  * 스캔작업을 진행시키는 메소드
	  */
	@Scheduled(cron="0 0/1 * * * *")
	public void scanFile(){
		if(checkEngineWorking()==false){
			logger.error("Engine is not working");
			logger.error("Scan Schedule is stop");
			return;
		}

		if(PropertiesData.licenseStatus==false){
			logger.error("License is expired");
			return;
		}

		if(ScanScheduleList.scanSchedule.size()<1){
			logger.debug("Not Have Schedule");
			return;
		}
		int scheduleSize = 0;
		resultSchedule(ScanScheduleList.scanSchedule,ScanScheduleList.scanSchedule.size());
		ScanScheduleList.scanSchedule = convertPendingPath(ScanScheduleList.scanSchedule,FilePath.scheduler);
		scheduleSize = ScanScheduleList.scanSchedule.size();

		if(pendingCheck()<1){
			logger.debug("Not Pending Schedule");
			return;
		}
		for(int i=0;i<scheduleSize;i++){
			if(ScanScheduleList.scanSchedule.get(i).getResult().equals("pending")) {
				initResCount();
				startScanFile(ScanScheduleList.scanSchedule.get(i));
			}
		}
		ScanScheduleList.scanSchedule=resultSchedule(ScanScheduleList.scanSchedule,scheduleSize);
		Collections.sort(ScanScheduleList.scanSchedule, new ScanSchedule());
		saveJsonFile(FilePath.scheduler,ScanScheduleList.scanSchedule);
	}

	/**
	  * 스캔 작업이 끝난 스케줄이 반복형인지 단발형인지 확인하고, 형식에 맞춰 지우거나 반복 주기를 적용시키는 메소드
	  * @param list 스케줄 리스트
	  * @param scheduleSize 스케줄의 개수
	  * @return list 스케줄의 변동사항이 적용된 스케줄 리스트
	  */
	public List<ScanSchedule> resultSchedule(List<ScanSchedule> list,int scheduleSize){
		for(int i=0;i<scheduleSize;i++){
			if(list.size()<=i){
				continue;
			}else if(list.get(i).getResult().equals("finish")){
				if(list.get(i).getType().equals("repeat")){
					repeatSave(i);
				}else {
					list.remove(i--);
				}
			}else{

				continue;
			}
		}
		return list;
	}

	/**
	  * 들어온 스케줄이 반복형인 경우 주기에 맞춰서 변형 적용시키는 메소드
	  * @param i 변경시킬 스케줄의 번호
	  */
	public void repeatSave(int i){
		ScanSchedule ss = ScanScheduleList.scanSchedule.get(i);
		Calendar cl = Calendar.getInstance();
		String reservationDate = ss.getReservationDate().substring(ss.getReservationDate().length()-5);
		reservationDate = SimpleDateFormatCore.sdf2.format(new Date())+" "+reservationDate;
		try {
			logger.debug(reservationDate);
			cl.setTime(SimpleDateFormatCore.sdf.parse(reservationDate));
//			cl.setTime(new Date());
			if(ss.getCycle().equals("weekly")){
				cl.add(Calendar.DATE,7);
			}else if(ss.getCycle().equals("monthly")){
				cl.add(Calendar.MONTH,1);
			}else{
				cl.add(Calendar.DATE,1);
			}
			ScanScheduleList.scanSchedule.get(i).setReservationDate(SimpleDateFormatCore.sdf.format(cl.getTime()));
			ScanScheduleList.scanSchedule.get(i).setResult("wait");
			ScanScheduleList.scanSchedule.get(i).setNo(PropertiesData.schedulerSeq++);
			PropertiesData.setScheduleReportSeq("scheduler");
			saveJsonFile(FilePath.scheduler,ScanScheduleList.scanSchedule);
		} catch (ParseException e) {
			logger.error("Repeat Schedule Setting ParseException: "+e.getMessage());
		}finally {
			cl=null;
		}
	}

	/**
	  * 현재 시간에 검사 해야 하는 스케줄의 갯수를 카운팅하는 메소드
	  * @return 검사를 진행해야 하는 스케줄 카운팅 갯수
	  */
	public int pendingCheck(){
		int count =0;
		for(int i=0;i<ScanScheduleList.scanSchedule.size();i++){
			if(ScanScheduleList.scanSchedule.get(i).getResult().equals("wait")){

			}else{
				++count;
			}
		}
		return count;
	}
	
	/**
	  * 스캔 항목이 가리키는 경로에 있는 파일들의 목록을 작성한 파일을 생성하는 메소드
	  * @param list 스케줄 리스트
	  * @param i 스케줄 번호
	  * @param schedulerPath file_list.json 경로
	  */
	public void putFileList(List<ScanSchedule> list,int i,String schedulerPath){
		FileWriter fw = null;
		String fileListPath = "";

		try {
			fileListPath=FilePath.getScanResFile(list.get(i).getTaskID(), "file_list");
			fw = new FileWriter(fileListPath);
			writeFilePath(list.get(i).getPath(), fw);
			fw.flush();
			fw.close();
			fileList.put(list.get(i).getNo(),fileListPath);
			saveFileList();
		} catch (IOException e) {
			logger.error("Check Time IOException: "+e.getMessage());
		} finally {
			saveJsonFile(schedulerPath,list);
			fw=null;
		}

	}

	/**
	  * wait 상태의 스케줄 증 검사시간이 되었을 경우 pending 상태로 전환 혹은 processing 상태로 강제종료된 경우 pending 상태로 전환시키는 메소드
	  * @param list 스케줄 리스트
	  * @param schedulerPath 검사할 스케줄의 폴더 혹은 파일의 경로
	  * @return list 변환된 스케줄 리스트
	  */
	public List<ScanSchedule> convertPendingPath(List<ScanSchedule> list, String schedulerPath){
		SimpleDateFormat sdf = SimpleDateFormatCore.sdf;
		Date date = new Date();
		String now = sdf.format(date);
		logger.debug("now: "+now);
		int result = -2;


		for(int i=0;i<list.size();i++) {
			if(list.isEmpty()){
				break;
			}
			try {
				result = sdf.parse(now).compareTo(sdf.parse(list.get(i).getReservationDate()));
			} catch (ParseException e) {
				logger.error("Check Time ParseException");
			}
			logger.debug(result+" "+ list.get(i).getResult().equals("wait"));

			if(result!=-1&&list.get(i).getResult().equals("wait")) {
				putFileList(list,i,schedulerPath);
				list.get(i).setResult("pending");
			}else if(list.get(i).getResult().equals("processing")){
				list.get(i).setResult("pending");
			}else{
				logger.debug("Not Scan Start Time: "+list.get(i).toString());
			}

			if(fileList.containsKey(list.get(i).getNo())==false&&list.get(i).getResult().equals("wait")==false){
				putFileList(list,i,schedulerPath);
			}

		}
		initFileList();

		return list;
	}

	/**
	  * 스케줄 데이터를 토대로 스캔시키는 메소드
	  * @param ss 진행할 스캐줄 정보
	  */
	public void startScanFile(ScanSchedule ss) {
		int index = ScanScheduleList.scanSchedule.indexOf(ss);
		ss.setResult("processing");
		ss.setStartDate(SimpleDateFormatCore.sdf.format(new Date()));
		ScanScheduleList.scanSchedule.set(index,ss);
		saveJsonFile(FilePath.scheduler,ScanScheduleList.scanSchedule);
		long listNo = ss.getNo();
		logger.debug("fileList: "+fileList.get(listNo));
		if(fileList.isEmpty()==false) {
			File file = new File(fileList.get(listNo));
			FileReader fr = null;
			BufferedReader bfr = null;
			String line;
			File scanFile = null;
			ScanResult sr = null;
			List<ScanResult> list = new ArrayList<ScanResult>();
			String logPath = null;
			String reportPath;
			try {
				fr = new FileReader(file);
				bfr = new BufferedReader(fr);
			} catch (FileNotFoundException e) {
				logger.error("Scanning FileNotFoundException: " + e.getMessage());
			}
			try {
				while ((line = bfr.readLine()) != null) {
					scanFile = new File(line);
					sr = new ScanResult();
					sr.setNo(list.size());
					sr.setDate(SimpleDateFormatCore.sdf.format(new Date()));
					sr.setFilepath(scanFile.getCanonicalPath());

					if (scanFile.exists() == false) {
						sr.setResult("Failed - ERROR 1010");
					} else if (scanFile.canRead() == false) {
						sr.setSize((scanFile.length() / 1024) + "kb");
						sr.setResult("Failed - ERROR 1011");
						resCount.put("Failed", resCount.get("Failed") + 1);
					} else if (line.equals(scanFile.getCanonicalPath()) == false) {
						sr.setSize((scanFile.length() / 1024) + "kb");
						sr.setName("Symbolic");
						sr.setResult("Failed - ERROR 1011");
						resCount.put("Failed", resCount.get("Failed") + 1);
					}else {
						Properties prop = new Properties();
						String check = null;
						logger.debug("검사 파일: " + file.getPath());
						check = scanFile.getCanonicalPath();
						byte[] encoding = check.getBytes("UTF-8");
						String checkPath = new String(encoding, "UTF-8");
						if(PropertiesData.engine==1) {
							sr = v3scanFile(prop, checkPath, sr, scanFile);
						}else{
							sr = vrsdkScanFile(prop,checkPath,sr,scanFile);
						}
						prop.clear();
					}
					if (sr != null) {
						list.add(sr);
					}
				}
				sr = null;
				logPath = FilePath.getScanResFile(ss.getTaskID(), "log");
				reportPath = FilePath.getScanResFile(ss.getTaskID(), "report");
				saveResultProp(reportPath);
				saveJsonFile(logPath, list);
				ss.setResult("finish");
				ss.setReport(logPath.replace(FilePath.tmpFolder + "/", ""));
				ss.setEndDate(SimpleDateFormatCore.sdf.format(new Date()));
				ScanScheduleList.scanSchedule.set(index, ss);
				saveJsonFile(FilePath.scheduler, ScanScheduleList.scanSchedule);
				saveReport(ss);

				File listFile = new File(fileList.get(listNo));
				logger.debug("deleteListFile: " + listFile.getCanonicalPath());
				PathAndConvertGson.deleteFile(listFile);
				fileList.remove(listNo);

			} catch (IOException e) {
				logger.error("Scan Read File IOException: " + e.getMessage());
			} finally {

				try {
					if (fr != null) {
						fr.close();
					}
					if (bfr != null) {
						bfr.close();
					}
					if (list != null) {
						list.clear();
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					fr = null;
					bfr = null;
					list = null;
				}
				saveFileList();
			}
		}else{

		}
	}

	public ScanResult v3scanFile(Properties prop, String checkPath, ScanResult sr,File scanFile){
		int result = V3Scanner.scanFile(checkPath, PropertiesData.scanOption,prop);
		logger.debug("result: " + result);
		logger.debug("result log: " + prop.getProperty(V3Const.PROP_KEY_MORE_INFO));
		if (result == 0) {
			sr = null;
			resCount.put("Normal", resCount.get("Normal") + 1);
		} else if (result == 1) {
			sr.setSize((scanFile.length() / 1024) + "kb");
			sr.setName(prop.getProperty(V3Const.PROP_KEY_MALWARE_CATEGORY));
			sr.setResult("Infected");
			resCount.put("Infected", resCount.get("Infected") + 1);
		} else if (result == 2 || result == 4) {
			sr.setSize((scanFile.length() / 1024) + "kb");
			sr.setName(prop.getProperty(V3Const.PROP_KEY_MALWARE_CATEGORY));
			sr.setResult("Cured");
			resCount.put("Cured", resCount.get("Cured") + 1);
		} else {
			sr.setSize((scanFile.length() / 1024) + "kb");
			sr.setName("Anonymous");
			sr.setResult("Failed - "+failReason(result));
			resCount.put("Failed", resCount.get("Failed") + 1);
		}

		return sr;
	}

	public ScanResult vrsdkScanFile(Properties prop, String checkPath, ScanResult sr, File scanFile){
		int result = ViRobotLog.scanFile(checkPath,PropertiesData.scanOption, prop);
		logger.debug("result: " + result);
		if(PropertiesData.scanOption==0) {
			if (result == 2) {
				sr = null;
				resCount.put("Normal", resCount.get("Normal") + 1);
			} else if (result == 3 || result ==4) {
				sr.setSize((scanFile.length() / 1024) + "kb");
				sr.setName(prop.getProperty(ViRobotLog.AVSB_MALWARE_NAME));
				sr.setResult("Infected");
				resCount.put("Infected", resCount.get("Infected") + 1);
			} else {
				sr.setSize((scanFile.length() / 1024) + "kb");
				sr.setName("Anonymous");
				sr.setResult("Failed - "+failReason(result));
				resCount.put("Failed", resCount.get("Failed") + 1);
			}
		}else{
			if (result == 2) {
				sr = null;
				resCount.put("Normal", resCount.get("Normal") + 1);
			} else if (result == 3 || result ==5 || result == 6 || result == 7 || result == 9) {
				sr.setSize((scanFile.length() / 1024) + "kb");
				sr.setName(prop.getProperty(V3Const.PROP_KEY_MALWARE_CATEGORY));
				sr.setResult("Cured");
				resCount.put("Cured", resCount.get("Cured") + 1);
			} else {
				sr.setSize((scanFile.length() / 1024) + "kb");
				sr.setName("Anonymous");
				sr.setResult("Failed - "+failReason(result));
				resCount.put("Failed", resCount.get("Failed") + 1);
			}
		}
		return sr;
	}

	public String failReason (int result){
		String reason = "";
		if(PropertiesData.scanOption==0){
			reason = detectFailReason(result);
		}else{
			reason = disinfectFailReason(result);
		}

		return reason;
	}

	public String detectFailReason(int result){
		String reason = "";
		if(PropertiesData.engine==1){
			switch(result){
				case -1:
					reason = "ERROR 1014";
					break;
				case -7:
					reason = "ERROR 1020";
					break;
				case -5:
					reason = "ERROR 1013";
					break;
				default:
					reason = "ERROR 1019";
					break;
			}
		}else{
			switch(result){
				case 1:
				case 6:
				case 7:
					reason = "ERROR 1014";
					break;
				case -42:
					reason = "ERROR 1013";
					break;
				default:
					reason = "ERROR 1019";
			}
		}
		return reason;
	}

	public String disinfectFailReason(int result){
		String reason = "";
		if(PropertiesData.engine==1){
			switch(result){
				case 3:
					reason = "ERROR 1021";
					break;
				case 5:
				case 6:
				case -1:
					reason = "ERROR 1014";
					break;
				case -7:
					reason = "ERROR 1020";
					break;
				case -5:
					reason = "ERROR 1013";
					break;
				default:
					reason = "ERROR 1019";
					break;
			}
		}else{
			switch(result){
				case 1:
					reason = "ERROR 1021";
					break;
				case 4:
				case 8:
				case 11:
					reason = "ERROR 1014";
					break;
				case -42:
					reason = "ERROR 1013";
					break;
				default:
					reason = "ERROR 1019";
					break;
			}
		}
		return reason;
	}

	/**
	  * 스케줄 검사 결과를 카운팅해 properties 파일로 저장하는 메소드
	  * @param path 저장할 파일의 경로
	  */
	public void saveResultProp(String path){
		FileOutputStream fos = null;
		Properties prop = new Properties();
		try {
			fos = new FileOutputStream(path);
			prop.setProperty("Normal",resCount.get("Normal").toString());
			prop.setProperty("Infected",resCount.get("Infected").toString());
			prop.setProperty("Cured",resCount.get("Cured").toString());
			prop.setProperty("Failed",resCount.get("Failed").toString());
			int total = resCount.get("Normal")+resCount.get("Infected")+resCount.get("Cured")+resCount.get("Failed");
			prop.setProperty("Total",String.valueOf(total));
			prop.store(fos,path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			fos = null;
			prop = null;

		}
	}

	/**
	  * 스케줄 검사 경로 하위에 존재하는 모든 파일의 절대경로를 file_list.txt 파일에 저장하는 메소드
	  * @param path 스케줄 검사 경로
	  * @param fw 스케줄 검사 경로 하위 파일들을 텍스트 파일로 저장시키는 객체
	  */
	public void writeFilePath(String path, FileWriter fw){
		File file = new File(path);
		try {
			if(file.isDirectory()){
				File[] files = file.listFiles();
				for(File scan:files) {
					writeFilePath(scan.getCanonicalPath(), fw);
				}
			}else{
				fw.write(path+"\n");
			}
		} catch (IOException e) {
			logger.error("Write File List IOException: "+e.getMessage());
		}
	}

	/**
	  * fileList 객체에 저장되어있는 스케줄러들의 file_list.txt 경로들을 JSON 파일로 모아놓는 메소드
	  */
	public void saveFileList(){
		FileWriter fw = null;
		File file = new File(FilePath.fileListPath);
		try {
			if(!file.exists()){
				file.createNewFile();
			}
			fw = new FileWriter(file);
			for(long no : fileList.keySet()){
				fw.write(no+","+fileList.get(no)+"\n");
			}

		} catch (IOException e) {
			logger.error("Save File IOException: "+e.getMessage());
		}finally {
			try {
				if(fw!=null) {
					fw.flush();
					fw.close();
				}
			} catch (IOException e) {
				logger.error("Resource Return IOException: "+e.getMessage());
			}finally {
				fw = null;
			}
		}
	}

	/**
	  * 스케줄러 검사 정보, 검사 결과를 출력한 파일에 대한 경로등의 정보를 report.json 파일에 저장시키는 메소드
	  * @param ss 스캐줄러 검사 정보
	  */
	public void saveReport(ScanSchedule ss){
		File file = new File(FilePath.report);
		FileWriter writer = null;
		if(file.exists()==false){
			try {
				file.createNewFile();
				writer = new FileWriter(file);
				writer.write("[]");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				writer=null;
			}
		}

		ScanSchedule scanInfo = setScanSchedule(ss);
		if(ScanScheduleList.scanReport.size()>0) {
			Collections.sort(ScanScheduleList.scanReport, new ScanSchedule());
		}
		while(ScanScheduleList.scanReport.size()>=Integer.parseInt(PropertiesData.reportCount)){
			deleteReportFile();
			ScanScheduleList.scanReport.remove(0);
		}
		ScanScheduleList.scanReport.add(scanInfo);
		saveJsonFile(FilePath.report,ScanScheduleList.scanReport);
	}

	/**
	  * ScanSchedule 객체에 담긴 스캔 데이터를 다른 ScanSchedule 객체에 저장하는 메소드
	  * @param ss 데이터를 담고 있는 ScanSchedule 객체
	  * @return 데이터를 옮긴 ScanSchedule 객체
	  */
	public ScanSchedule setScanSchedule(ScanSchedule ss){
		ScanSchedule scanInfo = new ScanSchedule();
		scanInfo.setNo(PropertiesData.reportSeq);
		PropertiesData.setScheduleReportSeq("report");
		scanInfo.setResult(ss.getResult());
		scanInfo.setReport(ss.getReport());
		scanInfo.setReservationDate(ss.getReservationDate());
		scanInfo.setStartDate(ss.getStartDate());
		scanInfo.setEndDate(ss.getEndDate());
		scanInfo.setPath(ss.getPath());

		return scanInfo;
	}

	/**
	  * 일정 수를 넘긴 report.txt, log.txt 파일을 삭제하는 메소드
	  */
	public void deleteReportFile(){
		ScanSchedule report = ScanScheduleList.scanReport.get(0);
		String reportLog = report.getReport();
		String reportReport = report.getReport().replace("log","report");
		File logFile = new File(FilePath.tmpFolder+"/"+reportLog);
		File reportFile = new File(FilePath.tmpFolder+"/"+reportReport);
		PathAndConvertGson.deleteFile(logFile);
		PathAndConvertGson.deleteFile(reportFile);
	}

	/**
	  * 리스트 데이터를 JSON 파일로 저장하는 메소드
	  * @param path 데이터를 저장할 경로
	  * @param list 저장할 데이터를 가지고 있는 리스트
	  */
	public static <T> void saveJsonFile(String path, List<T> list){
		FileWriter writer = null;
		File file = new File(path);
		try {
			if(!file.exists()){
				writer = new FileWriter(path);
				file.createNewFile();
				writer.write("");
				writer.flush();
				writer.close();
			}
			writer = new FileWriter(path);

			PathAndConvertGson.gson.toJson(list,writer);

		} catch (IOException e) {
			logger.error("saveJsonFile IOException: "+e.getMessage());
		}finally{
			try {
				if(writer!=null){
					writer.flush();
					writer.close();
				}
			} catch (IOException e) {
				logger.error("Resource Return IOException: "+e.getMessage());
			}finally {
				writer=null;
			}
		}
	}

	/**
	  * 엔진이 동작하고 있는지 확인하는 메소드
	  * @return 엔진 동작의 여부
	  */
	public boolean checkEngineWorking(){
		boolean result = true;
		String[][] commands={{"/bin/bash","-c","ps -e | grep v3scan"},
				{"/bin/bash","-c","ps -e | grep vrsdk"},
				{"/bin/bash","-c","ps -e | grep AL"},
				{"/bin/bash","-c","ps -e | grep nProtect"}
			};
		Process p=null;
		try {
			String line = null;
			for(String[] command:commands) {
				p = Runtime.getRuntime().exec(command);
				BufferedReader bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));

				while ((line = bfr.readLine()) != null) {
					if (line.contains("v3scan")) {
						break;
					}else if(line.contains("vrsdk")){
						break;
					}else if(line.contains("AL")){
						break;
					}else if(line.contains("nProtect")){
						break;
					}
				}
				if (line != null) {
					result=true;
					break;
				}else{
					result=false;
				}
			}
		} catch (IOException e) {
			logger.error("Engine checking failed IOException: "+e.getMessage());
			result = false;
		}
		logger.debug("engine status: "+result);
		return result;
	}
}
