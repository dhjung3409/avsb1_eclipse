package com.terais.avsb.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.terais.avsb.dto.ReadLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.service.SaveLogFileService;

@Service
public class SaveLogFileServiceImpl implements SaveLogFileService{
	
	private static final Logger logger = LoggerFactory.getLogger(SaveLogFileServiceImpl.class);
	private static final String LOCAL = "127.0.0.1";
	
	@Autowired
	NodeAndCurrentGetServiceImpl getService;
	
	public List<Object> getSaveLog(String ip){
		logger.debug("getSaveLog");		
		logger.debug(LOCAL);
		logger.debug(ip=ip.trim());
		logger.debug("{}",LOCAL!=ip);
		List<Object> logList = new ArrayList<Object>();
		if(PropertiesData.subIp.contains(ip)==false&&ip.equals(LOCAL)==false){
			logger.debug("Wrong IP");
			return logList;
		}
		String getLogs=null;
		String option = "resultlog";
		
		getLogs = getService.getRest(ip, "log", option);
		logger.debug("getLogs: "+getLogs);
		ReadLog[] read=new Gson().fromJson(getLogs,ReadLog[].class);
		logger.debug(read[0].toString());
		List<Object> log = null;
		for(ReadLog rl: read){
			log=new ArrayList<Object>();
			log = setList(log,rl,ip);
			logList.add(log);
		}

		return logList;
	}

	public List<Object> setList(List<Object> list,ReadLog rl,String ip){
		list.add(rl.getNo());
		list.add(rl.getDate());
		if(ip.equals("127.0.0.1")){
			list.add("LOCAL");
		}else {
			list.add(ip);
		}
		list.add(rl.getTarget());
		list.add(rl.getResult());
		return list;
	}

}
