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

@Service
public class PageLogServiceImpl implements PageLogService{

	private static final Logger logger = LoggerFactory.getLogger(PageLogServiceImpl.class);

	public List<ReadLog> getPageLog() {
		long beforeTime = System.currentTimeMillis();
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

		long afterTime = System.currentTimeMillis();
		long differTime = (afterTime-beforeTime);
		logger.debug("test time: "+differTime);
		logger.debug("logList: "+logList);
		return logList;
	}
	public Object getLogObject(ReadLog readLog){
		List<Object> log = new ArrayList<Object>();
		log.add(readLog.getNo());
		log.add(readLog.getDate());
		log.add(readLog.getClient_ip());
		log.add(readLog.getTarget());
		log.add(readLog.getResult());
		return log;
	}
	
}
