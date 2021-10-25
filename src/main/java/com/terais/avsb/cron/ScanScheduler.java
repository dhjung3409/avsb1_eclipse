package com.terais.avsb.cron;

import com.ahnlab.v3engine.V3Const;
import com.ahnlab.v3engine.V3Scanner;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.ScanScheduleList;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.dto.ScanResult;
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

@Component
public class ScanScheduler {

	private static final Logger logger = LoggerFactory.getLogger(ScanScheduler.class);

	private static Map<Long,String> fileList = new HashMap<Long, String>();

	private static Map<String,Integer> resCount = new HashMap<String, Integer>();


	public void initResCount(){
		resCount.put("Total",0);
		resCount.put("Normal",0);
		resCount.put("Infected",0);
		resCount.put("Cured",0);
		resCount.put("Failed",0);
	}

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

	@Scheduled(cron="0 0/1 * * * *")
	public void scanFile(){
		if(checkEngineWorking()==false){
			logger.error("Engine is not working");
			logger.error("Scan Schedule is stop");
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

	public void repeatSave(int i){
		ScanSchedule ss = ScanScheduleList.scanSchedule.get(i);
		Calendar cl = Calendar.getInstance();
		try {
			cl.setTime(SimpleDateFormatCore.sdf.parse(ss.getReservationDate()));
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
				logger.debug("Not Scan Start Time.");
			}

			if(fileList.containsKey(list.get(i).getNo())==false&&list.get(i).getResult().equals("wait")==false){
				putFileList(list,i,schedulerPath);
			}

		}
		initFileList();

		return list;
	}

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
						sr = null;
					} else if (scanFile.canRead() == false) {
						sr.setSize((scanFile.length() / 1024) + "kb");
						sr.setResult("Can Not Read File.");
						resCount.put("Failed", resCount.get("Failed") + 1);
					} else if (line.equals(scanFile.getCanonicalPath()) == false) {
						sr.setSize((scanFile.length() / 1024) + "kb");
						sr.setName("Symbolic");
						sr.setResult("Symbolic File");
						resCount.put("Failed", resCount.get("Failed") + 1);
					} else {
						Properties prop = new Properties();
						String check = null;
						logger.debug("검사 파일: " + file.getPath());
						int result;
						check = scanFile.getCanonicalPath();
						byte[] encoding = check.getBytes("UTF-8");
						String checkPath = new String(encoding, "UTF-8");
						result = V3Scanner.scanFile(checkPath, prop);
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
							sr.setResult("Failed");
							resCount.put("Failed", resCount.get("Failed") + 1);
						}
						prop = null;
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

	public void deleteReportFile(){
		ScanSchedule report = ScanScheduleList.scanReport.get(0);
		String reportLog = report.getReport();
		String reportReport = report.getReport().replace("log","report");
		File logFile = new File(FilePath.tmpFolder+"/"+reportLog);
		File reportFile = new File(FilePath.tmpFolder+"/"+reportReport);
		PathAndConvertGson.deleteFile(logFile);
		PathAndConvertGson.deleteFile(reportFile);
	}

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
