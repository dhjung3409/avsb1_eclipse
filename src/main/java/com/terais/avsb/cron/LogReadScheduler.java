package com.terais.avsb.cron;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.terais.avsb.core.CurrentLog;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.dto.ReadLog;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.module.LicenseCheck;
import com.terais.avsb.module.ReadLogPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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
	public static String LI_M2="1";

	/**
	 * 로그에 찍히는 필드 값을 담아두는 객체
	 */
	private static Map<String, String> logElement = new HashMap<String, String>();

	/**
	 * 로그 결과물 키 값에 번호 값을 담아두는 객체
	 */
	private static Map<String,Integer> resultMap = new HashMap<String,Integer>();

	/**
	 * 로그 결과물을 종류별로 나누어 카운팅 하는 객체
	 */
	private static Map<String, Integer> resultCount = new HashMap<String, Integer>();

	private static int logNum=0;

	/**
	 * Gson 라이브러리 객체
	 */
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Gson 객체에서 사용 할 List<ReadLog>의 TypeToken
	 */
	private static final Type type = new TypeToken<List<ReadLog>>() {}.getType();


	/**
	 * 로그 결과 카운팅 객체의 초기화
	 */
	public void setResultCount(){
		resultCount.put("normal",0);
		resultCount.put("infected",0);
		resultCount.put("disinfected",0);
		resultCount.put("deleteFail",0);
		resultCount.put("failed",0);
	}

	/**
	 * 로그 결과 키 값, 번호 값 Map 객체의 초기화
	 */
	public void setResultMap(){
		resultMap.put("NORMAL_FILE",0);
		resultMap.put("INFECTED",1);
		resultMap.put("SCAN_CURE_DELETE_FAIL_BY_CONFIGURE",2);
		resultMap.put("SCAN_FAILED",3);
		resultMap.put("SCAN_CURE_DELETE_SUCCESS",4);
		resultMap.put("Normal",5);
		resultMap.put("Infected",6);
		resultMap.put("InfectedSuspicious",7);
		resultMap.put("Error",8);
		resultMap.put("Skip",9);
		resultMap.put("Failed",10);
		resultMap.put("Disinfected",11);
		resultMap.put("Delete",12);
		resultMap.put("DeleteFail",13);
	}


	/**
	 * 로그 요소 생성 initMethod에서 한번 실행
	 */
	public void initLogElement(){
		logElement.put("target","target=");
		logElement.put("targetSize","targetSize=");
		logElement.put("ClientIP","ClientIP=");
		logElement.put("scanTime","scanTime=");
		logElement.put("scanResult","scanResult=");
	}

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
	public void readLog(){
		if(PropertiesData.licenseStatus==false){
			logger.debug("License is Expired: "+PropertiesData.licenseExpire);
			return;
		}

		ReadLogPath.readLogPath();
		if(PropertiesData.isEnginePath==false){
			logger.error("This is not engine path: "+PropertiesData.enginePath);
			return;
		}
		String logFolder = FilePath.logPath;
		String logFile = FilePath.logFile.substring(FilePath.logFile.lastIndexOf("/")+1);
		Properties prop = getResultProperties(FilePath.todayResult);

		try {
			logNum = Integer.parseInt(getProperties(prop, "num"));
		}catch(NumberFormatException e){
			logger.error("result properties num value is not number");
			logNum = 0;
		}
		setResultCount();
		System.out.println("logFolder : "+(logFolder));
		System.out.println("logFile : "+(logFile));
		if(logFolder.equals("")||logFile.equals("")){
			System.err.println("This is not log File");
			return;
		}

		File logDir = new File(logFolder);

		List<File> logList = LogReadScheduler.checkFile(logDir.listFiles(),logFile);

		Collections.sort(logList,new AscendingFile());

		File lastLog = new File(FilePath.tmpLogFile);

		List<String> lastLogList = addLogList(lastLog);

		List<String> newLastLog = new ArrayList<String>();

		List<ReadLog> readLog = new ArrayList<ReadLog>();
		for(File file : logList){
			if(checkLastLine(file,lastLogList,newLastLog)==false){
				continue;
			}
			System.out.println(file.getPath());
			try {
				divLogFile(file.getCanonicalPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(file.length()>(1024*1024)*20) {
				getLogString(readLog, lastLogList,newLastLog);
			}else{
				getReadLog(readLog,file,lastLogList,newLastLog);
			}

		}

		setResultProperties(prop);
		saveProperties(prop,FilePath.todayResult);
		if(prop!=null){
			prop.clear();
		}
		System.out.println("New Log List : "+newLastLog);
		writeFile(newLastLog);
		System.out.println("ReadLog List : "+readLog.toString());
		if(readLog.isEmpty()){
			logger.debug("ReadLog Not add");
		}else {
			getReadLog(gson, readLog);
			writeGson(readLog);
		}
	}

	/**
	 * @param file
	 * @return
	 */
	public List<String> addLogList(File file){
		List<String> logList = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader bfr = null;
		if(file.exists()==false){
			return logList;
		}
		try {
			fr = new FileReader(file);
			bfr = new BufferedReader(fr);
			String line = "";
			while((line= bfr.readLine())!=null){
				logList.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(fr !=null){
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if(bfr!=null){
				try {
					bfr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return logList;
	}

	/**
	 * @param file
	 * @param tmpLogList
	 * @param newLogList
	 * @return
	 */
	public boolean checkLastLine(File file,List<String> tmpLogList,List<String> newLogList){
		List<String> lastLogList = getLastLine(file);
		if(lastLogList.size()<1){
			return false;
		}
		for(String log : lastLogList){
			if(checkLastLineDetail(tmpLogList,log)==false){
				System.out.println("different log : "+log);
				return true;
			}
			newLogList.add(log);
		}
		return false;
	}

	/**
	 * @param tmpLogList
	 * @param log
	 * @return
	 */
	public boolean checkLastLineDetail(List<String> tmpLogList, String log){
		boolean result = false;
		for(String tmpLog : tmpLogList){
			if(tmpLog.contains(log)){
				result=true;
				break;
			}
		}
		return result;
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
			lastLines= CurrentLog.getLines(lastLines,5,raFile,raFileSize);
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
	 * @param path
	 * @return
	 */
	public static Properties getResultProperties(String path){
		Properties prop = new Properties();
		File file = new File(path);

		FileInputStream fis = null;
		try {
			if(file.getParentFile().canWrite()==false){
				System.err.println("Can not create file");
				return prop;
			}else if(file.getParentFile().canWrite()&&file.exists()==false){
				file.createNewFile();
			}
			fis = new FileInputStream(file);
			prop.load(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	/**
	 * @param prop
	 */
	public static void setResultProperties(Properties prop){
		int total = 0;
		for(String key : resultCount.keySet()){
			int propSum = 0;
			int propValue = 0;
			try{
				propValue = Integer.parseInt(getProperties(prop,key));
				System.out.println("Prop "+key+" : "+propValue);
				System.out.println("resultCount "+key+" : "+resultCount.get(key));
			}catch(NumberFormatException e){
				System.out.println("Key is not number");
			}
			propSum=resultCount.get(key)+propValue;
			total=total+propSum;
			resultCount.put(key,propSum);
			setProperties(prop,key, String.valueOf(propSum));
		}
		setProperties(prop,"total", String.valueOf(total));
		setProperties(prop,"num", String.valueOf(logNum));
	}

	/**
	 * @param prop
	 * @param key
	 * @return
	 */
	public static String getProperties(Properties prop,String key){
		return prop.getProperty(key);
	}

	/**
	 * @param prop
	 * @param key
	 * @param value
	 */
	public static void setProperties(Properties prop,String key,String value){
		prop.setProperty(key,value);
	}

	/**
	 * @param prop
	 * @param path
	 */
	public static void saveProperties(Properties prop,String path){
		File file = new File(path);
		FileOutputStream fos=null;
		try {
			fos = new FileOutputStream(file);
			prop.store(fos,path);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param lines
	 */
	public static void writeFile(List<String> lines){
		File file = new File(FilePath.tmpLogFile);
		FileWriter fw=null;
		try {
			fw = new FileWriter(file);
			for(String line : lines) {
				fw.write(line+"\n");
			}
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param readLog
	 */
	public static void writeGson(List<ReadLog> readLog){
		File file = new File(FilePath.readTodayLogFile);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			gson.toJson(readLog,fw);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(fw!=null){
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * @param gson
	 * @param readLog
	 */
	public void getReadLog(Gson gson,List<ReadLog> readLog){
		File file = new File(FilePath.readTodayLogFile);
		if(file.exists()==false){
			System.err.println("Not Exists File");
			return;
		}

		FileReader fr = null;
		List<ReadLog> fileLog = null;
		try {
			fr = new FileReader(file);
			JsonReader reader = new JsonReader(fr);
			fileLog = gson.fromJson(reader,type);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(fileLog!=null){
			readLog.addAll(fileLog);
		}

	}


	/**
	  * 로그파일의 모든 로그 정보를 가져오는 메소드
	  * @param readLog 읽어들인 로그를 저장하는 리스트
	  * @param file 읽어들일 로그 파일
	  * @return 로그 파일의 모든 로그 정보를 저장한 리스트
	  */
	public List<ReadLog> getReadLog(List<ReadLog> readLog, File file,List<String> lastLines,List<String> newLastline){
		FileReader fr = null;
		BufferedReader bfr = null;
		List<String> lineList = new ArrayList<String>();
		int lastCount = 0;
		boolean lastCheck = false;
		try {
			fr=new FileReader(file);
			bfr = new BufferedReader(fr);
			String line;
			while((line=bfr.readLine())!=null){
				if(line.contains(FilePath.dummyFile)){
					continue;
				}
				if(lastCheck==false){
					if(lastLines.contains(line)){
						lastCount++;
						System.out.println("lastCount : "+lastCount);
					}

					setReadLogList(readLog,line);

					if(lastCount>=5){
						lastCheck=true;
						readLog.clear();
						setResultCount();
					}
				}else{
					setReadLogList(readLog,line);
				}

				getLastLogList(lineList,line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			fr=null;
			bfr=null;
		}
		newLastline.addAll(lineList);
		return readLog;
	}

	/**
	  * 해당 파일이 지정된 로그 파일인지 확인하는 메소드
	  * @param files 확인할 파일의 리스트
	  * @return 확인된 로그파일들
	  */
	public static List<File> checkFile(File[] files,String logName) {
		List<File> returnFiles = new ArrayList<File>();
		try {
			for (File file : files) {
				String path = file.getCanonicalPath().toString();
				long fileSize = file.length();
				if (path.contains(logName) && fileSize > 0 && file.isFile() && file.canRead() && path.contains("swp") == false) {
					returnFiles.add(file);
				}
			}
			Collections.sort(returnFiles);
		} catch (Exception e) {
//            logger.error("checkFile error");
		}
		return returnFiles;
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
	public List<ReadLog> getLogString(List<ReadLog> list,List<String> lastLines,List<String> newlastLines){
		logger.debug("getLogString start");
		List<String> fileList = checkDivFile();
		List<String> lineList = new ArrayList<String>();
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

							if (line.contains(FilePath.dummyFile)) {
								continue;
							}

							if (lastCheck == false) {
								if (lastLines.contains(line)) {
									lastCount++;
								}
								setReadLogList(list,line);
								if (lastCount >= 5) {
									lastCheck = true;
									list.clear();
									setResultCount();
								}
							} else {
								setReadLogList(list,line);
							}
							getLastLogList(lineList,line);
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
		}
		logger.debug("getLogString end for start");
		newlastLines.addAll(lineList);
		delDivFile(fileList);
		logger.debug("getLogString end");
		return list;
	}

	/**
	 * @param logList
	 * @param line
	 */
	public static void setReadLogList(List<ReadLog> logList,String line){

		ReadLog readLog = setLogData(line);
		if(readLog.getResult()==null){
			return;
		}
		String scanResult = setResult(readLog.getResult());
		resultCount.put(scanResult,resultCount.get(scanResult)+1);
		if(scanResult.equals("normal")==false){
			++logNum;
			logList.add(readLog);
		}
	}

	/**
	 * @param line
	 * @return
	 */
	public static ReadLog setLogData(String line){
		String[] split = line.split(" ");
		ReadLog readLog = new ReadLog();
		readLog.setNo(logNum);
		String date = "";
		boolean check = false;
		for(String data : split){
			if(data.contains(logElement.get("scanTime"))){
				date=data;
				check = true;
			}else if(check){
				check=false;
				date = date+" "+data;
				setReadLog(readLog, date);
			}else {
				setReadLog(readLog, data);
			}
		}
		return readLog;
	}

	/**
	 * @param readlog
	 * @param data
	 */
	public static void setReadLog(ReadLog readlog,String data){
		if(data.contains(logElement.get("target"))){
			readlog.setTarget(data.replace(",","").replace(logElement.get("target"),""));
		}else if(data.contains(logElement.get("ClientIP"))){
			readlog.setClient_ip(data.replace(",","").replace(logElement.get("ClientIP"),""));
		}else if(data.contains(logElement.get("scanTime"))){
			readlog.setDate(data.replace(",","").replace(logElement.get("scanTime"),""));
		}else if(data.contains(logElement.get("scanResult"))){
			readlog.setResult(data.replace(",","").replace(logElement.get("scanResult"),""));
		}else{
			return;
		}
	}

	/**
	 * @param result
	 * @return
	 */
	public static String setResult(String result){
		String setting = null;
		switch (resultMap.get(result)){
			case 0:
			case 5:
				setting="normal";
				break;
			case 1:
			case 6:
			case 7:
				setting="infected";
				break;
			case 4:
			case 11:
			case 12:
				setting="disinfected";
				break;
			case 2:
			case 13:
				setting="deleteFail";
				break;
			case 3:
			case 8:
			case 9:
			case 10:
				setting="failed";
				break;
			default:
				break;
		}
		return setting;
	}

	/**
	 * @param lineList
	 * @param line
	 */
	public static void getLastLogList(List<String> lineList,String line){
		keepList(lineList);
		if(lineList.size()==5) {
			lineList.remove(0);
		}
		lineList.add(line);
	}

	/**
	 * @param lineList
	 */
	public static void keepList(List<String> lineList){
		while(lineList.size()>5){
			lineList.remove(0);
		}
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