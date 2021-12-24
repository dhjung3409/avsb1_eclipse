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

/**
  * 엔진이 출력한 로그를 가져오는 클래스
  */
@Service
public class SaveLogFileServiceImpl implements SaveLogFileService{

	private static final Logger logger = LoggerFactory.getLogger(SaveLogFileServiceImpl.class);
	
	/**
	 * LOCAL IP
	 */
	private static final String LOCAL = "127.0.0.1";
	
	/**
	 * bean 등록 된 NodeAndCurrentGetServiceImpl 클래스 객체
	 */
	@Autowired
	private NodeAndCurrentGetServiceImpl getService;
	
	/**
	  * REST API 요청으로 입력된 IP 서버들이 지니고 있는 로그 정보를 가져오는 메소드
	  * @param ip 로그 정보를 가지고 올 IP
	  * @return 입력된 IP 서버가 지니고 있던 로그 정보 리스트
	  */
	public List<Object> getSaveLog(String ip){
		logger.debug("getSaveLog");
		logger.debug(LOCAL);
		logger.debug(ip=ip.trim());
		logger.debug("{}",LOCAL!=ip);
		List<Object> logList = new ArrayList<Object>();
		if(getSubIP().contains(ip)==false&&ip.equals(LOCAL)==false){
			logger.debug("Wrong IP");
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

	/**
	  * 리스트에 로그에 대한 데이터를 세팅하는 메소드
	  * @param list 한 줄 로그에 대한 데이터가 저장될 리스트
	  * @param rl 한줄 로그에 대한 상세 데이터가 있는 ReadLog 객체
	  * @param ip 로그를 가지고 있는 IP
	  * @return 한 줄 로그에 대한 데이터들이 저장된 리스트
	  */
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

	/**
	  * 등록된 IP 서버에 대한 정보를 가지고 오는 메소드
	  * @return 서버에 등록된 IP 서버들
	  */
	public List<String> getSubIP(){
		List<String> setIP = new ArrayList<String>();
		for(String ip:PropertiesData.subIp){
			setIP.add(ip.substring(ip.indexOf("$")+1));
		}
		return setIP;
	}

}
