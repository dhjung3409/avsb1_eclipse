package com.terais.avsb.service.impl;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.dto.ReadLog;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.PageLogService;

/**
  * log.json 파일에 저장된 상세 검사 결과들을 통합해 가져오는 클래스
  */
@Service
public class PageLogServiceImpl implements PageLogService{

	private static final Logger logger = LoggerFactory.getLogger(PageLogServiceImpl.class);

	/**
	  * LOCAL 서버에서 NORMAL_FILE 검사 결과를 제외한 검사 결과를 저장해 놓은 log.json 데이터 통합해서 가져오는 메소드
	  * @return log.json 파일들에 저장된 데이터 통합 리스트
	  */
	public List<ReadLog> getPageLog() {
		Date date = new Date();
		Calendar cl = Calendar.getInstance();
		cl.setTime(date);
		cl.add(Calendar.DATE,-(Integer.parseInt(PropertiesData.dateTerm)));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		List<String> setDate = new ArrayList<String>();
		String fileName;
		while(cl.getTime().compareTo(date)!=1){
			String[] logDate = sdf.format(cl.getTime()).split("/");
			fileName=FilePath.getReadLogFile(logDate[0], logDate[1], logDate[2]);
			setDate.add(fileName);
			cl.add(Calendar.DATE,1);
		}
		List<ReadLog> logList = new ArrayList<ReadLog>();
		File file;
		try {
			for(String setFile : setDate) {
				file = new File(setFile);
				if(!file.exists()){
					logger.debug(file.getCanonicalPath());
					continue;
				}
				logger.debug("FilePath: "+file.getCanonicalPath());
				List<ReadLog> readLog = PathAndConvertGson.convertGson(setFile);
				logger.debug("readLog: "+readLog.toString());
				if(readLog==null){
					continue;
				}
				logList.addAll(readLog);
			}
		} catch (IOException e) {
			logger.error("Get Page Log IOException: "+e.getMessage());
		}
		logger.debug("logList: "+logList);
		return logList;
	}

	
}
