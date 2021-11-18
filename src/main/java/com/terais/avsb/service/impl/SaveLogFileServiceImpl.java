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
		logger.info("getSaveLog");
		logger.info(LOCAL);
		logger.info(ip=ip.trim());
		logger.info("{}",LOCAL!=ip);
		List<Object> logList = new ArrayList<Object>();
		if(setSubIP().contains(ip)==false&&ip.equals(LOCAL)==false){
			logger.info("Wrong IP");
			return logList;
		}
		String getLogs=null;
		String option = "resultlog";
		String httpIP="";
		for(String ipInfo:PropertiesData.subIp){
			if(ipInfo.substring(ipInfo.indexOf("$")+1).equals(ip)){
				httpIP=ipInfo;
				break;
			}
		}
		getLogs = getService.getRest(httpIP, "log", option);
		logger.info("getLogs: "+getLogs);
		ReadLog[] read=new Gson().fromJson(getLogs,ReadLog[].class);
		logger.info(read[0].toString());
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

	public List<String> setSubIP(){
		List<String> setIP = new ArrayList<String>();
		for(String ip:PropertiesData.subIp){
			setIP.add(ip.substring(ip.indexOf("$")+1));
		}
		return setIP;
	}

}
