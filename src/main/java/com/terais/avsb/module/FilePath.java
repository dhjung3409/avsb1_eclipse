package com.terais.avsb.module;

import com.terais.avsb.core.PathAndConvertGson;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
  * AVSB 구동에 필요한 폴더, 파일 경로를 저장한 클래스
  */
public class FilePath {
	// "/  => separator + " "
	public static String defaultName = PathAndConvertGson.getOSFolder();
	/**
	 * 로그 폴더 경로
	 */
	public static String logFolder = defaultName+"/log";
	
	/**
	 * info 폴더 경로
	 */
	public static String infoFolder = defaultName+"/info";
	
	/**
	 * 설정 폴더 경로
	 */
	public static String configFolder = defaultName+"/config";
	
	/**
	 * libs 폴더 경로
	 */
	public static String libsFolder = defaultName+"/libs";
	
	/**
	 * 계정 폴더 경로
	 */
	public static String accountFolder = defaultName+"/account";
	
	/**
	 * 임시 폴더 경로
	 */
	public static String tmpFolder = defaultName+"/tmp";
	
	/**
	 * 시스템 로그 폴더 경로
	 */
	public static String log4jLogFolder = logFolder+"/system";
	
	/**
	 * 로그 Repository 폴더 경로
	 */
	public static String repoFolder = logFolder+"/repo";
	
	/**
	 * 로그인 정보 파일 경로
	 */
	public static String accountFile = accountFolder+"/login.json";
	
	/**
	 * 로그인 정보 카운팅 파일 경로
	 */
	public static String accountCountFile = accountFolder+"/login_seq.ini";
	
	/**
	 * 설정 파일 경로
	 */
	public static String configFile = configFolder+"/config.properties";
	
	/**
	 * IP 설정 파일 경로
	 */
	public static String IpConfigFile = configFolder+"/ip_config.properties";
	
	/**
	 * v3engine 로그 저장 폴더
	 */
	public static String logPath = "";
	
	/**
	 * v3engine 로그 파일
	 */
	public static String logFile = "";
	
	/**
	 * v3scan_server 파일 경로
	 */
	public static String v3File = "";
	
	/**
	 * 금일 날짜 log.json 파일 경로
	 */
	public static String readTodayLogFile = null;

//	/**
//	 * 하우리 검사 결과 로그파일이 저장되는 폴더 경로
//	 */
//	public static String hauriLogPath = "";
//
//	/**
//	 * 하우리 검사 결과 로그 파일 경로
//	 */
//	public static String hauriLogFile = "";

	/**
	 * 검사에 필요한 폴더 내부의 파일 목록이 저장된 텍스트 파일 경로
	 */
	public static String directoryFileList = "";
	
	/**
	 * 마지막에 확인한 로그 목록이 저장된 .log_tmp.json 파일 경로
	 */
	public static String tmpLogFile = logFolder+ File.separator+".log_tmp.log";
	
	/**
	 * 로그 읽기 상태, 로그 파일 개수를 담고있는 workers.ini 파일 경로
	 */
	public static String workName = infoFolder+"/workers.ini";
	
	/**
	 * 금일 날짜 result 파일 경로
	 */
	public static String todayResult = null;
	
	/**
	 * 스케줄 정보를 가지고 있는 scheduler.json 경로
	 */
	public static String scheduler = tmpFolder+"/scheduler.json";
	
	/**
	 * 검사가 끝난 스케줄 정보를 가지고 있는 report.json 경로
	 */
	public static String report = tmpFolder+"/report.json";
	
	/**
	 * 임시 라이브러리 폴더 경로
	 */
	public static String tmpLib = tmpFolder+"/lib";
	
	/**
	 * 라이센스 파일 경로
	 */
	public static String license = defaultName+"/license.txt";

	/**
	 * scheduler.json, report.json 파일의 번호를 지니고 있는 Properties 파일 경로
	 */
	public static String tmpSeq = tmpFolder+"/.scheduler_report_seq.properties";
	
	/**
	 * 엔진 경로를 가지고 있는 Properties 파일 경로
	 */
	public static String enginePathFile = libsFolder+"/engine_path.properties";
	
	/**
	 * file_list 정보를 담고있는 JSON 파일 경로
	 */
	public static String fileListPath = tmpFolder+"/file_list.json";
	
	/**
	 * v3properties 파일 경로
	 */
	public static String v3properties = (libsFolder+"/vendor/ahnlab/V3Scanner.properties").trim();

	/**
	 *
	 */
	public static String vrsdkProperties = (libsFolder+"/vendor/hauri/avsb.properties").trim();
	
	/**
	 * v3engine/v3daemon/option.cfg 파일 경로
	 */
	public static String v3option = null;
	
	/**
	 * option.cfg 파일의 복사본 경로
	 */
	public static String copyV3option = null;
	
	/**
	 * 라이센스 기간의 대시 기호
	 */
	public static String LI_DA="/";

	/**
	 * 엔진 검사용 더미 파일
	 */
	public static String dummyFile = "TERA_SYSTEM_CHECK_DUMMY_FILE";
	
	/**
	  * 입력된 날짜(log/repo/yyyy/MM/dd)의 repo 폴더의 경로를 가져오는 메소드
	  * @param year 연도
	  * @param month 월
	  * @param day 일
	  * @return 입력된 날짜(log/repo/yyyy/MM/dd)의 repo 폴더의 경로
	  */
	public static String getDatePath(String year, String month, String day){
		return FilePath.repoFolder+"/"+year+"/"+month+"/"+day;
	}
	
	/**
	  * 입력된 날짜(log/repo/yyyy/MM)의 repo 폴더의 경로를 가져오는 메소드
	  * @param year 연도
	  * @param month 월
	  * @return 입력된 날짜(log/repo/yyyy/MM)의 repo 폴더의 경로
	  */
	public static String getDatePath(String year, String month){
		return FilePath.repoFolder+"/"+year+"/"+month;
	}
	
	/**
	  * 입력된 날짜(log/repo/yyyy)의 repo 폴더의 경로를 가져오는 메소드
	  * @param year 연도
	  * @return 입력된 날짜(log/repo/yyyy/MM)의 repo 폴더의 경로
	  */
	public static String getDatePath(String year){
		return FilePath.repoFolder+"/"+year;
	}
	
	/**
	  * 입력된 날짜의 log.json 파일의 경로를 가져오는 메소드
	  * @param year 연도
	  * @param month 월
	  * @param day 일
	  * @return log.json의 경로
	  */
	public static String getReadLogFile(String year, String month, String day){
		return getDatePath(year,month,day)+"/log.json";
	}
	
	/**
	  * 스캔 결과 파일들의 경로를 가져오는 메소드
	  * @param taskID - String - 스케쥴러 고유의 taskID
	  * @param name - String - 결과물 파일의 이름 ex) report, log, file_list 등
	  * @return 결과 파일의 경로
	  */
	public static String getScanResFile(String taskID,String name){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		return tmpFolder+"/"+sdf.format(date)+"_"+taskID+"_"+name+".text";
	}

	
}