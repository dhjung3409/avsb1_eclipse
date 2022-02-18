package com.terais.avsb.core;

import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.cron.LogReadScheduler;
import com.terais.avsb.cron.ScanScheduler;
import com.terais.avsb.cron.SubIPCheckScheduler;
import com.terais.avsb.module.DefaultAccount;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
  * AVSB 내부에서 사용되는 static 객체를 모아놓은 클래스
  */
public class PropertiesData {

	private static final Logger logger = LoggerFactory.getLogger(PropertiesData.class);

	/**
	 * API 사용 여부
	 */
	public static String useApi = null;
	
	/**
	 * 설치된 경로
	 */
	public static String installPath = null;
	
	/**
	 * 설치한 날짜
	 */
	public static String installDate = null;
	
	/**
	 * 저장이 가능한 리포트의 개수
	 */
	public static String reportCount = null;
	
	/**
	 * 페이지와 연결된 IP 목록
	 */
	public static Set<String> subIp = new HashSet<String>();
	
	/**
	 * 해당 IP와 연결여부 목록
	 */
	public static Map<String,Boolean> ipConnect =new HashMap<String, Boolean>();
	
	/**
	 * 솔루션이 사용하는 포트
	 */
	public static String port = null;
	
	/**
	 * 엔진 로그파일의 개수
	 */
	public static String worker = null;
	
	/**
	 * 로그 읽기 상태
	 */
	public static String logStatus = null;
	
	/**
	 * 계정 등록 번호
	 */
	public static long accountCount = -1;
	
	/**
	 * 로그 데이터 저장 기간 설정
	 */
	public static String dateTerm = null;
	
	/**
	 * 악성코드 감염 현황 표시 갱신 주기
	 */
	public static String resReloadTime = null;
	
	/**
	 * 실시간 검사 현황 표시 갱신 주기
	 */
	public static String currentReloadTime = null;
	
	/**
	 * 실시간 검사 로그 표시 갱신 주기
	 */
	public static String logReloadTime = null;
	
	/**
	 * 사용하는 엔진 종류
	 */
	public static String useEngine = "NoneEngine";
	
	/**
	 * 라이센스의 유효성 여부
	 */
	public static boolean licenseStatus = false;
	
	/**
	 * 라이센스 만료 한달 전 여부
	 */
	public static boolean licenseMonthStatus = true;
	
	/**
	 * 라이센스 잔여 날짜
	 */
	public static int licenseRemain = -1;
	
	/**
	 *  검사 스케줄러 등록 번호
	 */
	public static int schedulerSeq = 0;

	/**
	 * 검사 결과물 등록 번호
	 */
	public static int reportSeq = 0;
	
	/**
	 * 엔진 종류
	 */
	public static int engine = 0;
	
	/**
	 * 엔진 경로(엔진 업데이트 기능 추가 시 프로퍼티에 따로 저장해둘 필요 있음)
	 */
	public static String enginePath = null;
	
	/**
	 * 올바른 엔진 경로 여부 확인
	 */
	public static boolean isEnginePath=true;

	/**
	 * 예약 스캐시 사용되는 옵션
	 */
	public static int scanOption = 0;
	
	/**
	 * 라이센스 기간
	 */
	public static String licenseExpire = PasswordAlgorithm.LI_Y1+ SubIPCheckScheduler.LI_Y2+PathAndConvertGson.LI_Y3+CurrentLog.LI_Y4+FilePath.LI_DA+ ScanScheduler.LI_M1+ LogReadScheduler.LI_M2+FilePath.LI_DA+ CurrentCountScheduler.LI_D1+ScanScheduleList.LI_D2;
	
	/**
	 * OS 종류
	 */
	public static String osName = null;
	
	/**
	 * SSL 인증서 사용으로 HTTP 혹은 HTTPS 여부
	 */
	public static String HTTP = "https://";
	
	/**
	  * 설정 프로퍼티에 저장된 데이터를 가져오는 메소드
	  */
	public static void callConfig(){
		String configFile = FilePath.configFile;
		Properties prop = getProp(configFile);
		try {
			useApi = prop.get("use_api").toString();
			port = prop.get("port").toString();
			installPath = prop.get("install_path").toString();
			installDate = prop.get("install_day").toString();
			dateTerm = prop.get("date_term").toString();
			resReloadTime = prop.get("res_reload_time").toString();
			currentReloadTime = prop.get("current_reload_time").toString();
			logReloadTime = prop.get("log_reload_time").toString();
			reportCount = prop.get("report_count").toString();
			HTTP=prop.get("HTTP").toString();
		}catch(NullPointerException e){
			logger.error("Call Config NullPointerException: "+e.getMessage());
			setErrorConfig(prop,configFile);
		}catch(Exception e){
			logger.error("Call Config Error: "+e.getMessage());
			setErrorConfig(prop,configFile);
		}
		prop.clear();
		
	}
	
	/**
	  * 설정 파일을 불러오는 중 파일 손상으로 문제가 생겼을 때 입력되는 기본 값들
	  * @param prop - 설정 파일 프로퍼티 데이터
	  * @param path - 설정 파일 경로
	  */
	public static void setErrorConfig(Properties prop,String path){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		useApi = prop.get("usr_api")==null?setValue(prop,"usr_api","0"):prop.getProperty("usr_api");
		port = prop.get("port")==null?setValue(prop,"port","12394"):prop.getProperty("port");
		installPath = prop.get("install_path")==null?setValue(prop,"install_path",System.getProperty("user.dir")):prop.getProperty("install_path");
		installDate = prop.get("install_day")==null?setValue(prop,"install_day",sdf.format(date)):prop.getProperty("install_day");
		dateTerm = prop.get("date_term")==null?setValue(prop,"date_term","30"):prop.getProperty("date_term");
		resReloadTime = prop.get("res_reload_time")==null?setValue(prop,"res_reload_time","5"):prop.getProperty("res_reload_time");
		currentReloadTime = prop.get("current_reload_time")==null?setValue(prop,"current_reload_time","10"):prop.getProperty("current_reload_time");
		logReloadTime=prop.get("log_reload_time")==null?setValue(prop,"log_reload_time","5"):prop.getProperty("log_reload_time");
		reportCount = prop.get("report_count")==null?setValue(prop,"report_count","30"):prop.getProperty("report_count");
		HTTP = prop.get("HTTP")==null?setValue(prop,"HTTP","https://"):prop.getProperty("HTTP");

		setProp(prop,path);

	}
	
	/**
	  * 설정 값을 Properties에 저장하는 메소드
	  * @param prop - 저장할 Properties
	  * @param key - 저장할 Properties의 키값
	  * @param value - 저장해야 하는 설정 값
	  * @return String
	  */
	public static String setValue(Properties prop,String key,String value){
		prop.setProperty(key,value);
		return value;

	}
	
	/**
	  * 연결할 IP 정보를 불러오는 메소드
	  */
	public static void callSubIp(){
		String ipConfigFile = FilePath.IpConfigFile;		
		Properties prop = getProp(ipConfigFile);
		try {
			String ips = prop.get("sub_ip").toString();
			for (String ip : ips.split(",")) {
				logger.debug("Add Sub IP: "+ip);
				if (!ip.equals("")&&RegularExpression.checkIP(ip.substring(ip.indexOf("$")+1).trim())) {
					subIp.add(ip.trim());
				}
			}
		}catch(NullPointerException e){
			logger.error("Call SubIP NullPointerException: "+e.getMessage());
			if(subIp.size()==0){
				subIp.add(HTTP+"/127.0.0.1");
			}
		}catch (Exception e){
			logger.error("Call SubIP Error: "+e.getMessage());
			if(subIp.size()==0){
				subIp.add(HTTP+"/127.0.0.1");
			}
		}
		prop.clear();
	}
	
	/**
	  * Properties에 저장된 로그파일을 읽어들이는 상태, 로그파일의 개수를 불러오는 메소드
	  */
	public void callWorker(){
		String workerFile = FilePath.workName;
		Properties prop = getProp(workerFile);
		try {
			worker = prop.get("worker").toString();
			logStatus = prop.get("status").toString();
		}catch (NullPointerException e){
			logger.error("Call Worker NullPointerException: "+e.getMessage());
			worker="0";
			logStatus = "Initialization: normal";
		}catch (Exception e){
			logger.error("Call Worker Error: "+e.getMessage());
			worker="0";
			logStatus = "Initialization: normal";
		}
		prop.clear();
	}

	/**
	  * 스케줄러, 리포트 등록 번호를 불러들이는 메소드
	  */
	public static void callSchedulerSeq(){
		Properties prop = getProp(FilePath.tmpSeq);
		try {
			schedulerSeq = Integer.parseInt(prop.get("scheduler").toString());
			reportSeq = Integer.parseInt(prop.get("report").toString());
			logger.debug("reportSeq: "+reportSeq);
		}catch(NullPointerException e){
			logger.error("Call Scheduler Seq NullPointerException: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}catch(NumberFormatException e){
			logger.error("Call Scheduler Seq NumberFormatException: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}catch (Exception e){
			logger.error("Call Scheduler Seq Error: "+e.getMessage());
			setErrorSchedule("scheduler");
			setErrorSchedule("report");
		}
		prop.clear();
	}

	/**
	  * 스케줄러, 리포트 등록 번호 불러오기를 파일 손상으로 실패했을 때, 최근 등록 값을 불러오고 파일에 저장해놓는 메소드
	  * @param type "scheduler" "report" 불러올 파일의 타입을 저장한 객체
	  */
	public static void setErrorSchedule(String type){
		List<ScanSchedule> ss = null;
		if(type.equals("scheduler")) {
			ss = PathAndConvertGson.convertGson(FilePath.scheduler);
		}else {
			ss = PathAndConvertGson.convertGson(FilePath.report);
		}
		int no = 0;
		for(ScanSchedule scan : ss){
			if(scan.getNo()>no){
				no= (int) scan.getNo();
			}
		}
		if(type.equals("scheduler")){
			schedulerSeq= no;
		}else{
			reportSeq=no;
		}
	}

	/**
	  * 스케줄러, 리포트 최근 등록 번호를 저장하는 메소드
	  * @param type "scheduler" "report" 불러올 파일의 타입을 저장한 객체
	  */
	public static void setScheduleReportSeq(String type){
		Properties prop = getProp(FilePath.tmpSeq);

		if(type.equals("scheduler")){
			logger.debug("scheduler: "+PropertiesData.schedulerSeq);
			prop.setProperty("scheduler", String.valueOf(PropertiesData.schedulerSeq));
		}else{
			logger.debug("report: "+PropertiesData.reportSeq);
			prop.setProperty("report", String.valueOf(++PropertiesData.reportSeq));
		}
		setProp(prop,FilePath.tmpSeq);
	}
	
	/**
	  * 계정 등록 번호를 불러오는 메소드
	  */
	public static void callAccountSeq(){
		String accountSeq = FilePath.accountCountFile;
		Properties prop = getProp(accountSeq);
		try {
			accountCount = Long.parseLong(prop.get("no").toString());
		}catch (NullPointerException e){
			logger.error("Call Account Seq NullPointerException: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}catch (NumberFormatException e){
			logger.error("Call Account Seq NumberFormatException: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}catch (Exception e){
			logger.error("Call Account Seq Error: "+e.getMessage());
			accountCount = DefaultAccount.getNo();
		}
		prop.clear();
	}

	/**
	  * 엔진 이름을 불러오는 메소드
	  */
	public static void callEngineInfo(){
		String engineFile = FilePath.enginePathFile;
		Properties prop = getProp(engineFile);
		try {
			if(engine==0) {
				engine = Integer.parseInt(prop.getProperty("engine"));
			}else{
				logger.debug("engine setting already");
			}

			if(engine==1){
				enginePath=prop.getProperty("ahnlab_engine");
			}else if(engine==2){
				enginePath=prop.getProperty("hauri_engine");
			}else if(engine==3){
				enginePath=prop.getProperty("alyac_engine");
			}else if(engine==4){
				enginePath=prop.getProperty("tachyon_engine");
			}else{
				enginePath="";
			}

			scanOption=Integer.parseInt(prop.getProperty("scan_option","0"));
		}catch(NullPointerException e){
			logger.error("Call Engine Info NullPointerException: "+e.getMessage());

			engine = 0;
			enginePath = "";
		}catch(Exception e){
			logger.error("Call Engine Info Error: "+e.getMessage());
			engine = 0;
			enginePath = "";
		}
		prop.clear();
	}

	/**
	  * Properties를 불러오는 메소드
	  * @param filePath Properties의 경로
	  * @return 불러온 Properties
	  */
	public static Properties getProp(String filePath){
		Properties pro = new Properties();
		FileInputStream fis = null;
		try {
			fis=new FileInputStream(filePath);
			pro.load(fis);
		} catch (FileNotFoundException e) {
			logger.error("getProperties FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("getProperties IOException: "+e.getMessage());
		}finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				logger.error("Get Properties FileInputStream IOException: "+e.getMessage());
			}
		}

		return pro;
	}

	/**
	  * Properties를 저장하는 메소드
	  * @param pro Properties 데이터가 저장되어있는 객체
	  * @param path Properties 데이터를 저장할 경로
	  */
	public static void setProp(Properties pro, String path){
		File file = new File(path);
		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(file);
			pro.store(fos,path);

		} catch (FileNotFoundException e) {
			logger.error("setProperties FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("setProperties IOException: "+e.getMessage());
		}finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				logger.error("Set Properties FileOutputStream IOException: "+e.getMessage());
			}
		}
	}
	
}
