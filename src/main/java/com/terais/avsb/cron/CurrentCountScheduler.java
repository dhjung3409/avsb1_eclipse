package com.terais.avsb.cron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.SimpleDateFormatCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.terais.avsb.module.FilePath;

/**
  * 실시간 검사 현황을 일정 주기로 확인하는 클래스
  */
@Component
public class CurrentCountScheduler {

	private static final Logger logger = LoggerFactory.getLogger(CurrentCountScheduler.class);

	/**
	 * 라이센스 기간 중 연도의 십의 자리 수
	 */
	public static String LI_D1="3";

	/**
	 * 일정 주기 간격으로 카운팅 한 실시간 검사 수를 저장한 리스트
	 */
	private static List<Integer> countList = new ArrayList<Integer>();	


	/**
	  * 실시간 겁사 현황을 가져오는 메소드
	  * @return 실시간 검사현황
	  */
	public static List<Integer> getCountList(){
		return countList;
	}

	/**
	  * countList 기본값을 부여하는 메소드
	  */
	public static void initList(){
		while(countList.size()<6){
			countList.add(0);
		}		
	}

	/**
	  * 실시간 검사 수를 확인해 countList에 저장하는 메소드
	  */
	public void countCurrent(){
		if(PropertiesData.licenseStatus==false){
			logger.debug("License is Expired: "+PropertiesData.licenseExpire);
			countList.clear();
			initList();
			return;
		}
		logger.debug("Thread: {}",Thread.currentThread().getName());
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		SimpleDateFormat nowDate = SimpleDateFormatCore.sdf;
		String fileName=FilePath.logFile;
		File file = null;
		BufferedReader bfr = null;
		FileReader fr = null;
		try {

			Calendar cl = Calendar.getInstance();
			cl.setTime(date);
			List<String> dateList = new ArrayList<String>();

			for(int i=2;i<=10;i+=7){
				cl.add(Calendar.SECOND, -i);
				Date secondDate = cl.getTime();
				dateList.add(sdf.format(secondDate));
			}
			file=new File(fileName);
			if(!file.exists()){
				logger.debug("Not exists File");
				return;
			}
			fr = new FileReader(file);
			bfr = new BufferedReader(fr);
			logger.debug("dateList: "+dateList);
			String checkDate = nowDate.format(date);
			String checkTime = null;
			String line = null;
			int lineCount = 0;
			int count = 0;
			while((line=bfr.readLine())!=null){
				if(line.contains(FilePath.dummyFile)){
					continue;
				}
				lineCount++;

				if(line.contains(checkDate)){
					String[] split = line.split(" ");
					checkTime = split[0]+" "+split[1];
					if(sdf.parse(dateList.get(1)).compareTo(sdf.parse(checkTime))!=1&&sdf.parse(dateList.get(0)).compareTo(sdf.parse(checkTime))!=-1){
						count++;
						split=null;
						checkTime=null;
					}
				}

				line = null;
			}

			logger.debug("ddd : "+lineCount);
			logger.debug("count: "+count);
			countList.remove(0);			
			countList.add(count);
			
		} catch (FileNotFoundException e) {
			logger.error("CurrentCount FileNotFoundException Error: "+e.getMessage());
		} catch (IOException e) {
			logger.error("CurrentCount IOException Error: "+e.getMessage());
		} catch (Exception e){
			logger.error("CurrentCount Exception Error: "+e.getMessage());
		}finally{
			try {
				if(fr!=null){
					fr.close();
				}
				if(bfr!=null) {
					bfr.close();
				}
			} catch (IOException e) {
				logger.error("Resource Return IOException: "+e.getMessage());
			}finally {
				fr=null;
				bfr=null;
			}
		}
	}	
	
}
