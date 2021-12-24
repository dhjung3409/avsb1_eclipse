package com.terais.avsb.cron;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.terais.avsb.core.CurrentLog;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.module.LicenseCheck;
import com.terais.avsb.module.ReadLogPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.dto.ReadLog;
import com.terais.avsb.module.FilePath;


/**  
* 저장된 로그를 읽어들여 JSON파일로 저장, 파일 검사의 결과값을 각 날짜별로 나눠 저장 <br>
* NORMAL_FILE - 정상파일 / INFECTED - 감염파일 / SCAN_CURE_DELETE_SUCCESS - 치료성공 / SCAN_FAILED - 검사실패 / SCAN_CURE_DELETE_FAIL_BY_CONFIGURE - 치료실패
*/
@Component
public class LogReadScheduler {

	private static final Logger logger = LoggerFactory.getLogger(LogReadScheduler.class);

	/**
	 * 라이센스 기간 중 달의 일의 자리 수
	 */
	public static String LI_M2="5";

	/**
	 * Gson 객체에서 사용 할 List<ReadLog>의 TypeToken
	 */
	private static final Type type = new TypeToken<List<ReadLog>>() {}.getType();

	/**
	  * 라이센스 상태를 확인하는 스케줄러
	  */
	@Scheduled(cron="0 5 0 * * *")
	public void getLicenseStatus(){
		logger.debug("licenseCheck");
		LicenseCheck.checkPeriod();
	}

	/**
	  * 날짜를 갱신해 파일을 생성하는 스케줄
	  */
	@Scheduled(cron="0 58 23 * * *")
	public void setTodayFile(){
		logger.debug("Date Change");
		Date date = new Date();
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE,1);
		Date nextDate = cl.getTime();
		setDateFile(nextDate);
	}

	/**
	  * 날짜에 맞춰 파일이 제대로 생성되었는지 확인하는 스케줄러
	  */
	@Scheduled(cron="0 2 0 * * *")
	public void checkTodayFile(){
		logger.debug(FilePath.todayResult);
		Date date = new Date();
		SimpleDateFormat today = new SimpleDateFormat("yyyy/MM/dd");
		String stringToday = today.format(date);
		logger.debug("stringToday: "+stringToday);
		logger.debug("Date todayResult set today: "+(FilePath.todayResult.contains(stringToday)));
		if(FilePath.todayResult.contains(stringToday)==false){
			nowSetDateFile();
		}
	}

	/**
	  * 로그 정보를 저장하는 금일자 파일을 생성하는 메소드
	  */
	public void nowSetDateFile(){
		Date date = new Date();
		setDateFile(date);
	}

	/**
	  * 파일 생성 메소드
	  * @param date 파일을 생성할 날짜
	  */
	public void setDateFile(Date date){
		SimpleDateFormat today = new SimpleDateFormat("yyyy/MM/dd");
		String[] todayCheck = today.format(date).split("/");

		FilePath.todayResult=FilePath.getDatePath(todayCheck[0],todayCheck[1],todayCheck[2])+"/result";
		FilePath.readTodayLogFile=FilePath.getDatePath(todayCheck[0],todayCheck[1],todayCheck[2])+"/log.json";
	}



	/**
	  * 보관 일수가 지나간 파일을 삭제하는 스케줄러
	  */
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteLogFile(){
		Date date = new Date();
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE,-(Integer.parseInt(PropertiesData.dateTerm)));
		File file = new File(FilePath.repoFolder);
		checkRepoFolder(file,cl.getTime());
	}

	/**
	  * 일정 날짜 이전의 repo 폴더 안의 로그 정보들을 빈값으로 전환해 데이터를 지우는 메소드
	  * @param file 확인 해야되는 파일
	  * @param date 보존 해야 하는 마지막 날짜
	  */
	public void checkRepoFolder(File file,Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String checkDate = sdf.format(date);
		File[] files = file.listFiles();
		boolean check = false;
		FileWriter fw = null;
		try {
			for(File repoFile:files){
				check = getFileDate(repoFile.getCanonicalPath(),checkDate,sdf);
				if(repoFile.isDirectory()&&check==false){
					checkRepoFolder(repoFile,date);
				}else if(check==true){
					break;
				}else if(repoFile.getCanonicalPath().contains(".license")){
					break;
				}else{
					logger.debug(repoFile.getCanonicalPath());
					if(repoFile.canWrite()&&repoFile.length()>0) {
						fw = new FileWriter(repoFile.getCanonicalPath());
						fw.write("");
						fw.flush();
						fw.close();
					}
				}
			}
		} catch (IOException e) {
			logger.error("IOException repo failed: "+e.getMessage());
		}
	}

	/**
	  * 지정된 파일 경로의 날짜를 확인용 날짜와 비교해서 이전인지 이후인지 확인하는 메소드
	  * @param filePath repo/yyyy/MM/dd의 날짜 형식을 가지는 파일 경로
	  * @param checkDate yyyy/MM/dd 형식의 확인해야하는 날짜
	  * @param sdf yyyy/MM/dd 형식을 가지는 날짜 포멧 객체
	  * @return 날짜의 전후를 표시하는 boolean 값 (파일 날짜가 체크 날짜보다 이전이라면 false 이후라면 true)
	  */
	public boolean getFileDate(String filePath,String checkDate,SimpleDateFormat sdf){
		filePath = filePath.substring(filePath.indexOf("repo"));
		filePath = filePath.replace("repo/","");
		if(filePath.contains("log.json")){
			filePath = filePath.replace("/log.json","");
		}else if(filePath.contains("result")){
			filePath = filePath.replace("/result","");
		}
		boolean afterDate = false;
		try {
			afterDate = sdf.parse(checkDate).before(sdf.parse(filePath));
		} catch (ParseException e) {
			logger.error("parse failed: "+e.getMessage());
		}

		return afterDate;
	}

	/**
	  * 읽어들인 로그의 정보를 날짜별로 남기기 위해서 금년과 내년 날짜의 폴더를 생성하는 메소드
	  */
	@Scheduled(cron="0 0 0/6 * * *")
	public void makeResultDirectory() {
		logger.debug("Thread: {}",Thread.currentThread().getName());
		logger.debug("makedirectory start");
		Date date = new Date();
		SimpleDateFormat year = new SimpleDateFormat("yyyy");		
		
		String yearCheck = year.format(date);	
				
		String dirPath = FilePath.logFolder;
		String filePath = FilePath.repoFolder;

		File jsonDirectory = new File(dirPath);
	    File directory= new File(filePath);
	    
	    try{
	    	if(!jsonDirectory.exists())jsonDirectory.mkdir();
		    if(!directory.exists())directory.mkdir();
		    
			for(int i=0;i<2;i++){
			   	File repoDir = new File(filePath+"/"+(Integer.parseInt(yearCheck)+i));
			   	if(!repoDir.exists())repoDir.mkdir();
			   	for(int j=1;j<=12;j++){
			   		int days=31;
			   		if(j==4||j==6||j==9||j==11)days=30;
			   		else if(j==2&&(Integer.parseInt(yearCheck)+i)%4==0&&(Integer.parseInt(yearCheck)+i)%400!=0)days=29;
			   		else if(j==2||(Integer.parseInt(yearCheck)+i)%400==0)days=28;
			   		String month=j<10?"0"+j:String.valueOf(j);
			   		File monthDir = new File(filePath+"/"+(Integer.parseInt(yearCheck)+i)+"/"+month);
			   		if(!monthDir.exists())monthDir.mkdir();
			   		if(j==2)logger.debug((Integer.parseInt(yearCheck)+i)+"/"+month+"/"+days);
			   		for(int k=0;k<days;k++){
			   			String day=k<9?"0"+(k+1):String.valueOf(k+1);
			   			File dayDir = new File(filePath+"/"+(Integer.parseInt(yearCheck)+i)
			   					+"/"+month+"/"+day);
			   			if(!dayDir.exists())dayDir.mkdir();						
			   		}
			   	}
		   	
			}
	   }catch(Exception e){
		   logger.error("Dir Error: "+e);
	   }
	}

	/**
	  * 로그 정보를 읽어들여 저장하는 메소드
	  */
	@Scheduled(cron="10 0/5 * * * *")
	public void readLog() {
		if(PropertiesData.licenseStatus==false){
			logger.debug("License is Expired: "+PropertiesData.licenseExpire);
			return;
		}

		ReadLogPath.readLogPath();
		if(PropertiesData.isEnginePath==false){
			logger.error("This is not engine path: "+PropertiesData.enginePath);
			return;
		}
		List<String> divFiles = checkDivFile();
		delDivFile(divFiles);

		logger.debug("Thread: {}",Thread.currentThread().getName());
		logger.debug("readLog start");
		File dirFile = new File(FilePath.logPath);
		logger.debug(dirFile.getPath());
		String workName = FilePath.workName;
		
		String logFile = FilePath.readTodayLogFile;
		String tmpLogJson = FilePath.tmpLogJson;
		
		String resultName = FilePath.todayResult;				
		File f = new File(resultName);
		Properties addResult = new Properties();
		if(!f.exists()){
			FileOutputStream fos=null;
			try {
				f.createNewFile();
				addResult.setProperty("total", "0");
				addResult.setProperty("normal", "0");
				addResult.setProperty("infected", "0");
				addResult.setProperty("disinfected", "0");
				addResult.setProperty("failed", "0");
				addResult.setProperty("deleteFail", "0");
				fos = new FileOutputStream(f);
				addResult.store(fos, resultName);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				logger.error("create new result file failed");
			}finally{
				try {
					if(fos!=null) {
						fos.close();
					}
				} catch (IOException e) {
					logger.error("Result File Output IOException: "+e.getMessage());
				}
			}
		}
		Properties p = new Properties();  		
		
		int normal=0;
		int infected=0;
		int disinfected=0;
		int failed=0;
		int deleteFail=0;
		Map<String,String> logToLogJson=new HashMap<String,String>();
		Map<String,String> mappingKeyLogtoJson=new HashMap<String,String>();
		Map<String,Integer> resultKeyAndValue=new HashMap<String,Integer>();
		
		Map<String, String> resultText = new HashMap<String,String>();

		Properties pro = new Properties();   
		int worker=0;
		String status=null;
		FileOutputStream fos=null;
		FileInputStream fis=null;
    	
    	File workFile = new File(workName);
    	List<File> files=null;
		boolean checkNull=false;
    	try { 
    		files = checkFile(dirFile.listFiles());
    		Properties workPro=null;
	    	if(!workFile.exists()){
	    		workFile.createNewFile();
	    		workPro = new Properties();
	    		
	    		workPro.setProperty("worker", String.valueOf(files.size()));
	    		workPro.setProperty("status", "normal");
	    		fos = new FileOutputStream(workFile);	    		
				workPro.store(fos, workName);				
	    		fos=null;
	    		new PropertiesData().callWorker();
	    	}else if(PropertiesData.worker==null){
	    		new PropertiesData().callWorker();
	    	}
	    	
	    	if(Integer.parseInt(PropertiesData.worker)!=files.size()){
	    		logger.debug("Before Save worker: "+PropertiesData.worker);
	    		workPro = new Properties();	    		
	    		workPro.setProperty("worker", String.valueOf(files.size()));
	    		workPro.setProperty("status", PropertiesData.logStatus);
	    		fos = new FileOutputStream(workFile);	    		
				workPro.store(fos, workName);				
	    		fos=null;
	    		PropertiesData.worker=String.valueOf(files.size());
	    	}
	    	
	    	logger.debug("After Save worker: "+PropertiesData.worker);
	    	worker = Integer.parseInt(PropertiesData.worker);
	    	status = PropertiesData.logStatus;
	    	if(worker==0){
				logger.debug("Not exist LogFile");
				return;
			}
	    	fis=null;
    	} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Worker Properties Check IOException: "+e.getMessage());
		}
    	
    	   		
    	
    	logger.debug("Thread: "+worker);
		boolean checkLastLine;
		List<String> newTempList=null;
		List<ReadLog> logJsonList=null;
		List<String> readLog=null;
		try{
			if(files!=null){
				Collections.sort(files,new AscendingFile());
			}

	    	File logJson=new File(logFile);
	    	File jsonTemp = new File(tmpLogJson);
	    	
	    	if(!logJson.exists()){
	    		logJson.createNewFile();
				initJson(logJson);
	    	}

	    	if(!jsonTemp.exists()){
	    		jsonTemp.createNewFile();
				initJson(jsonTemp);
	    	}

	    	JsonReader logJsonReader=new JsonReader(new FileReader(logJson));

	    	JsonReader jsonTempReader = new JsonReader(new FileReader(jsonTemp));

			logJsonList =new Gson().fromJson(logJsonReader, type);

			if(logJsonList==null){
				logJsonList=new ArrayList<ReadLog>();
			}

	    	List<String> jsonTempList = new Gson().fromJson(jsonTempReader, List.class);

			if(jsonTempList==null){
				jsonTempList = new ArrayList<String>();
			}


	    	newTempList = new ArrayList<String>();

	    	checkNull = status.equals("normal");

	    	for ( int i=0; i< worker; i++){
				logger.debug("readLogFile: "+files.get(i).getCanonicalPath());
    			if(f.exists()){    		  
    	    		try{
    	    			fis = new FileInputStream(resultName);
    	    			p.load(fis);
    	    			normal = Integer.parseInt(p.get("normal").toString());
    	    	    	infected = Integer.parseInt(p.get("infected").toString());
    	    	    	disinfected = Integer.parseInt(p.get("disinfected").toString());
    	    	    	failed = Integer.parseInt(p.get("failed").toString());
    	    	    	deleteFail=Integer.parseInt(p.get("deleteFail").toString());	    	    	    	
    	    		}catch(Exception e ){
    	    			logger.error("ReadLog Prop load Exception: "+e.getMessage());
    	    		}    		
    	    	}
    			
    			File file = new File(files.get(i).getPath());	    					
    	
    			List<String> lastLine=getLastLine(file);
    			logger.debug("lastLines: "+lastLine.toString());
    			logger.debug("jsonTempListttt" +
						".: "+jsonTempList.toString());

    			int lineCount = 0;
    			for(int num=0;num<lastLine.size();num++){
    				checkLastLine = jsonTempList.contains(lastLine.get(num));
    				if(checkLastLine==false){
						logger.debug(lastLine.get(num));
    				}else {
						logger.debug(lastLine.get(num));
						newTempList.add(lastLine.get(num));
    					++lineCount;
    				}
    			}

    			if(lineCount==lastLine.size()){
    				logger.debug("file pass");
    				continue;
    			}
    			
    			logger.debug("continue next");

				divLogFile(files.get(i).getCanonicalPath());
    			resultKeyAndValue.put("NORMAL_FILE", normal);
    			resultKeyAndValue.put("INFECTED", infected);
    			resultKeyAndValue.put("SCAN_CURE_DELETE_SUCCESS", disinfected);
    			resultKeyAndValue.put("SCAN_FAILED", failed);
    			resultKeyAndValue.put("SCAN_CURE_DELETE_FAIL_BY_CONFIGURE", deleteFail);
    						
    			resultText.put("SCAN_FAILED", "failed");
    			resultText.put("SCAN_CURE_DELETE_SUCCESS", "disinfected");
    			resultText.put("INFECTED", "infected");
    			resultText.put("NORMAL_FILE", "normal");	
    			resultText.put("SCAN_CURE_DELETE_FAIL_BY_CONFIGURE", "deleteFail");
    			
    			logToLogJson.put("date", "scanTime=");
    			logToLogJson.put("target", "target=");
    			logToLogJson.put("client_ip", "ClientIP=");
    			logToLogJson.put("result", "scanResult=");
    			
    			
    			if(file.exists()){	

    				readLog=new ArrayList<String>();
    				if(file.length()>(1024*1024*20)){
    					logger.debug("readLog if");
						readLog = getLogString(readLog,jsonTempList);
					}else {
    					logger.debug("readLog else");
						readLog = getReadLog(readLog,file);

					}
					logger.info("readLog Size: "+readLog.size());
					int readCheck = -1;
					for(String checkString : jsonTempList){
						int checkInt = readLog.indexOf(checkString);
						if(readCheck<checkInt){
							readCheck=checkInt;
						}
					}
					logger.debug(readCheck+"");
					if(readLog.size()==(readCheck+1)){
						newTempList.addAll(lastLine);
						continue;
					}
    				String line=null;
    				int cnt=0;
					logger.debug("last Read Log: "+readLog.get(readLog.size()-1));
    				for(readCheck=readCheck+1; readCheck<readLog.size(); readCheck++){
    					line=readLog.get(readCheck);
    					++cnt;
    					ReadLog readLine= insertLog(line,logToLogJson,resultKeyAndValue,mappingKeyLogtoJson,logJsonList);


						for(ReadLog log :logJsonList){
							if(readLine==null){
								break;
							}else if(readLine.getDate().equals(log.getDate()) && readLine.getTarget().equals(log.getTarget()) && readLine.getResult().equals(log.getResult())){
								readLine=null;
								break;
							}
						}

    					if(readLine!=null){
							if(readLine!=null) {
								logJsonList.add(readLine);
							}
    					}
    				}
    				logger.debug("logJsonList: "+logJsonList.toString());
    				String tempLine = null;
    				logger.debug(readLog.isEmpty()+"");
    				int readLogSize = readLog.size()>5?5:readLog.size();
    				for(int lastCount=0;lastCount<readLogSize;lastCount++){
						tempLine=readLog.get(readLog.size()-1-lastCount);
						logger.debug("tempLine"+tempLine);
						newTempList.add(tempLine);
    				}

					logger.debug("newTempList: "+newTempList.toString());
    				int total=0;
					for(String key : resultKeyAndValue.keySet()){
						addResult.setProperty(resultText.get(key), resultKeyAndValue.get(key).toString());
						total += resultKeyAndValue.get(key);
					}
					addResult.setProperty("total", String.valueOf(total));
					fos = new FileOutputStream(resultName);
					addResult.store(fos, resultName);	
    				fos=null;
    			}

    		}

			logger.info("readLog End");
		}catch(NullPointerException e){
			logger.error("NullPointerException: "+e.getCause());
			initJson(tmpLogJson,logFile,pro,workName);
		}catch(JsonParseException e){
			logger.error("JsonParseException: "+e.getMessage());
			initJson(tmpLogJson,logFile,pro,workName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException: "+e.getMessage());
		}finally{
			FileWriter lastWriter = null;
			FileWriter jsonWriter = null;
			try {
				if(readLog!=null){
					readLog.clear();
				}
				lastWriter = new FileWriter(tmpLogJson);
				jsonWriter = new FileWriter(logFile);
				if(checkNull==false){
					pro.setProperty("worker", PropertiesData.worker);
					pro.setProperty("status", "normal");
					PropertiesData.logStatus="normal";
					fos = new FileOutputStream(workName);
					pro.store(fos, workName);
				}

				PathAndConvertGson.gson.toJson(newTempList,lastWriter);
				PathAndConvertGson.gson.toJson(logJsonList,jsonWriter);
				lastWriter.flush();
				jsonWriter.flush();
				lastWriter.close();
				jsonWriter.close();
			} catch (IOException e) {
				logger.error("LogRead Data Return IOException");
			}finally {
				readLog = null;
				logToLogJson = null;
				mappingKeyLogtoJson = null;
				resultKeyAndValue = null;
				newTempList = null;
				logJsonList = null;
				fos = null;
				fis = null;
				jsonWriter = null;
				lastWriter = null;

			}
		}
	}

	/**
	  * 로그파일의 모든 로그 정보를 가져오는 메소드
	  * @param readLog 읽어들인 로그를 저장하는 리스트
	  * @param file 읽어들일 로그 파일
	  * @return 로그 파일의 모든 로그 정보를 저장한 리스트
	  */
	public List<String> getReadLog(List<String> readLog, File file){
		FileReader fr = null;
		BufferedReader bfr = null;
		try {
			fr=new FileReader(file);
			bfr = new BufferedReader(fr);
			String line;
			while((line=bfr.readLine())!=null){
				if(line.contains(FilePath.dummyFile)){
					continue;
				}
				readLog.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			fr=null;
			bfr=null;
		}
		return readLog;
	}


	/**
	  * log.json 파일과 .log_tmp.json 파일이 손상된 경우 빈 JSON 파일로 바꾸는 메소드
	  * @param tmpLogJson .log_tmp.json 파일의 경로
	  * @param logFilePath log.json 파일의 경로
	  * @param pro worker.ini 파일에 저장할 정보를 가지고 있는 Properties
	  * @param workName worker.ini 파일의 경로
	  */
	public void initJson(String tmpLogJson, String logFilePath, Properties pro, String workName){
		File logFile = new File(logFilePath);
		File tmpFile = new File(tmpLogJson);
    	FileOutputStream fos=null;
		try {		
			logger.debug("cathchError start");
			pro.setProperty("worker",PropertiesData.worker);
			pro.setProperty("status", "NullError OR JsonParseError");
			PropertiesData.logStatus="NullError OR JsonParseError";
			fos = new FileOutputStream(workName);
			pro.store(fos, workName);
			initJson(tmpFile);
			initJson(logFile);
	    	logger.debug("catchError end");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("catchError(): FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("catchError(): IOException: "+e.getMessage());
		}finally {
			fos=null;
		}
	}

	/**
	  * 해당 파일을 빈 JSON 파일로 생성하는 메소드
	  * @param file 빈 JSON 파일로 만들 파일
	  */
	public void initJson(File file){
		FileWriter fw=null;
		try {
			logger.debug("Init File: "+file.getCanonicalPath());
			fw = new FileWriter(file);
			fw.write("[]");
			fw.flush();
			logger.debug("Init File end");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("catchError(): FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("catchError(): IOException: "+e.getMessage());
		}finally{
			try {
				if(fw!=null) {
					fw.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("catchError(): IOException: "+e.getMessage());
			}finally {
				fw=null;
			}

		}
	}

	/**
	  * 로그의 result 값을 분류해 카운팅 하고 그 값을 result 파일에 저장, Normal 이외의 결과값을 log.json 파일에 저장하는 메소드
	  * @param line 읽어들인 로그 한 줄
	  * @param logToLogJson result 파일에 로그 결과값을 분류하는 Map 객체
	  * @param resultKeyAndValue 분류된 결과값을 카운팅하는 Map 객체
	  * @param mappingKeyLogToJson 로그 한 줄의 날짜, 결과값, IP, 경로의 값들을 저장한 Map 객체
	  * @param logJsonList ReadLog 객체에 맞춰서 로그의 정보를 저장한 리스트
	  * @return 읽어들인 로그 한 줄의 정보를 담은 ReadLog 객체
	  */
	public ReadLog insertLog(String line,Map<String,String> logToLogJson,Map<String,Integer> resultKeyAndValue,Map<String,String> mappingKeyLogToJson,List<ReadLog> logJsonList){
		ReadLog readLog=new ReadLog();	
		if(line==null)return null;
		List<String> logs = new ArrayList<String>(Arrays.asList(line.split(" ")));
		int num=0;

		for(int k=0;k<logs.size();k++){								
			String log = logs.get(k);
			for (String key : logToLogJson.keySet()) {
				if(log.indexOf(logToLogJson.get(key))!=-1){
					log=getItemsLog(num,log,logs,logToLogJson.get(key));
					mappingKeyLogToJson.put(key, log);
					if(key=="result"){
						mapPlus(resultKeyAndValue,log);
						logger.debug(log+": "+resultKeyAndValue.get(log));
					}
				}								
			}								
			num++;
		}

		if(mappingKeyLogToJson.get("result").equals("NORMAL_FILE")){
			mappingKeyLogToJson.clear();
			logs.clear();
			return null;
		}
		logger.debug("result: "+mappingKeyLogToJson.get("result"));
		readLog.setClient_ip(mappingKeyLogToJson.get("client_ip"));
		readLog.setDate(mappingKeyLogToJson.get("date"));
		readLog.setResult(mappingKeyLogToJson.get("result"));
		readLog.setTarget(mappingKeyLogToJson.get("target"));
		long no = logJsonList.isEmpty()?0:logJsonList.size()+1;
		readLog.setNo(no);
		mappingKeyLogToJson.clear();
		logs.clear();
		logs=null;
		return readLog;
	}

	/**
	  * 해당 파일이 v3scan_res.log 파일인지 확인하는 메소드
	  * @param files 확인할 파일의 리스트
	  * @return 확인된 로그파일들
	  */
	public static List<File> checkFile(File[] files){
		List<File> returnFiles = new ArrayList<File>();
		try{
			for(File file:files){
				String path = file.getPath().toString();
				long fileSize = file.length();
				if(path.indexOf("v3scan_res.log")!=-1&&fileSize>0&&file.isFile()&&file.canRead()&&path.indexOf("swp")==-1){
					returnFiles.add(file);
				}		
			}
			Collections.sort(returnFiles);
		}catch(Exception e){
			logger.error("checkFile error");
		}
		return returnFiles;
	}

	/**
	  * 검사 결과를 카운팅하는 메소드
	  * @param resultKeyAndValue 결과 카운팅 값을 가지고 있는 객체
	  * @param key 결과의 종류
	  */
	public void mapPlus(Map<String,Integer> resultKeyAndValue,String key){
		int scanResult = resultKeyAndValue.get(key)+1;
		resultKeyAndValue.put(key, scanResult);
	}

	/**
	  * 키값을 배제한 로그값을 추출하는 메소드
	  * @param num 리스트에 존재하는 값의 위치
	  * @param log 추출해야 하는 로그 값
	  * @param logs 필요한 로그 값이 저장되어있는 리스트
	  * @param item 키값
	  * @return 추출한 로그 값
	  */
	public String getItemsLog(int num, String log, List<String> logs, String item){
		log=log.replace(item, "");

		if(item=="target="){
			log=getTarget(num,logs,log);
		}else if(item=="scanTime="){
			log+=" "+logs.get(++num).toString();
		}
		
		log=log.replace(",", "");
		
		return log;
	}

	/**
	  * Target 경로를 구하기 위한 함수
	  * @param num 리스트 내에 필요한 로그 값을 가지고 있는 위치
	  * @param logs 필요한 로그 값이 저장되어있는 리스트
	  * @param log 추출해야 하는 로그 값
	  * @return 추출한 Target 경로 로그 값
	  */
	public String getTarget(int num, List<String> logs, String log){
		try{

			if(logs.get(num).indexOf(",")!=-1)return log;			
			
			for(int i=num+1;i<logs.size();i++){
				log+=" "+logs.get(i).toString();
				if(logs.get(i).indexOf(",")!=-1)break;			
			}
			
		}catch(Exception e){
			logger.error("getTarget() error");
		}
		return log;
	}

	/**
	  * 로그 파일 내부에 저장된 로그 중 중복 체크를 위해 사용 될 마지막 5 라인을 추출하는 메소드
	  * @param file 로그 파일
	  * @return 추출된 마지막 5 라인
	  */
	public List<String> getLastLine(File file){
		List<String> lastLines=new ArrayList<String>();
		RandomAccessFile raFile;
		long raFileSize=0;
		try {
			raFile = new RandomAccessFile(file,"r");
			raFileSize = raFile.length()-1;
			raFileSize--;
			lastLines=CurrentLog.getLines(lastLines,5,raFile,raFileSize);
			if(raFile!=null) {
				raFile.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("LastLine FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("LastLine IOException: "+e.getMessage());
		}finally {
			raFile=null;
			raFileSize=0;
		}
		return lastLines;
	}

	/**
	  * 용량이 큰 로그 파일을 분할시키는 메소드
	  * @param logFile 로그파일의 경로
	  */
	public void divLogFile(String logFile){
		File file = new File(logFile);
		if(file.length()>(1024*1024)*20){
			int page = 0;
			String divFilePath = FilePath.tmpLib+"/log.txt";
			String filePath=divFilePath+"."+page;
			File divFile = new File(filePath);
			FileWriter fw = null;
			FileReader fr = null;
			BufferedReader bfr=null;
			try {
				fw = new FileWriter(divFile);
				fr = new FileReader(file);
				bfr = new BufferedReader(fr);
				String line;
				int count = 0;
				while((line=bfr.readLine())!=null){
					if(line.contains(FilePath.dummyFile)){
						continue;
					}
					++count;
					fw.write(line+"\n");
					if(count==100000){
						fw.flush();
						page++;
						filePath=divFilePath+"."+page;
						divFile=new File(filePath);
						fw=new FileWriter(divFile);
						count=0;
					}
				}
				fw.flush();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				fw=null;
				bfr=null;
				fr=null;
			}
		}else{
			file=null;
		}
	}

	/**
	  * 분할된 로그파일을 확인하는 메소드
	  * @return 분할시킨 로그 파일 리스트
	  */
	public List<String> checkDivFile(){
		File folder = new File(FilePath.tmpLib);
		File[] files=folder.listFiles();
		List<String> divFiles = new ArrayList<String>();
		try {
			for (int i = 0; i < files.length; i++) {
				if (files[i].getName().contains("log.txt") == true && files[i].getName().contains("swp") == false) {
					try {
						divFiles.add(files[i].getCanonicalPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}catch(NullPointerException e){
			logger.error("checkDivFile NullPointerException: "+e.getMessage());
		}
 		return divFiles;
	}

	/**
	  * 분할시켰던 로그파일을 삭제하는 메소드
	  * @param fileList 분할시켰던 로그파일 리스트
	  */
	public void delDivFile(List<String> fileList){
		File file = null;
		for(int i=0;i< fileList.size();i++){
			file = new File(fileList.get(i));
			if(file.exists()) {
				file.delete();
			}
		}
	}

	/**
	  * 분할된 로그파일이 지닌 로그를 담아내는 메소드
	  * @param list 로그를 담을 리스트
	  * @param lastLines 중복 검사 확인을 위한 이전 마지막 읽어낸 로그
	  * @return 로그가 담긴 리스트
	  */
	public List<String> getLogString(List<String> list,List<String> lastLines){
		logger.debug("getLogString start");
		List<String> fileList = checkDivFile();
		File file = null;
		FileReader fr = null;
		BufferedReader bfr = null;
		String fileName = fileList.get(0);
		boolean lastCheck=false;
		int lastCount=0;
		for(int i=0;i<fileList.size();i++){
			logger.debug("getLogString for start");
			fileName=fileName.substring(0,fileName.lastIndexOf(".")+1)+i;
			logger.debug("Log File Name : "+fileName);
			if(fileList.contains(fileName)) {
				file = new File(fileName);
				logger.debug(fileName);
				if (file.exists()) {
					try {
						fr = new FileReader(file);
						bfr = new BufferedReader(fr);
						String line;
						while ((line = bfr.readLine()) != null) {
							if (lastCheck == false) {
								if (lastLines.contains(line)) {
									lastCount++;
								}

								if (lastCount >= 5) {
									lastCheck = true;
								}
							} else {
								if (line.contains(FilePath.dummyFile)) {
									continue;
								} else {
									list.add(line);
								}
							}
						}
						logger.debug("getLogString end while start");
					} catch (IOException e) {
						logger.error("DivLogRead IOException: "+e.getMessage());
					} finally {
						fr = null;
						bfr = null;
					}
				}
			}
			if(lastCheck==false&&i==fileList.size()-1){
				i=-1;
				lastCheck=true;
			}
		}
		logger.debug("getLogString end for start");

		delDivFile(fileList);
		logger.debug("getLogString end");
		return list;
	}

}


/**
  * 배열 정렬 클래스
  */
class AscendingFile implements Comparator<File> { 

	/**
	  * 내림차순 정렬 메소드
	  * @param a 비교변수 a
	  * @param b 비교변수 b
	  * @return 비교 결과값
	  */
	public int compare(File a, File b) {			
		return b.compareTo(a); 
	} 
}