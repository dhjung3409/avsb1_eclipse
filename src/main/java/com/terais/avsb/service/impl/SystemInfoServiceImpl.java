package com.terais.avsb.service.impl;


import com.ahnlab.v3engine.V3Const;
import com.ahnlab.v3engine.V3Scanner;
import com.google.gson.Gson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.lib.ViRobotLog;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.module.IpListSortByIp;
import com.terais.avsb.module.RestURI;
import com.terais.avsb.service.SystemInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

/**
  * 시스템에 대한 정보를 가져오는 메소드
  */
@Service
public class SystemInfoServiceImpl implements SystemInfoService {

	private static final Logger logger = LoggerFactory.getLogger(SystemInfoServiceImpl.class);

	/**
	  * 서버 OS 정보를 가져오는 메소드
	  * @return 서버 OS 정보
	  */
	public Map<Object, Object> getServerInfo() {
		String os = System.getProperty("os.name");
		String arch = System.getProperty("os.arch");
		String version = System.getProperty("os.version");
		Map<Object, Object> serverInfo=new HashMap<Object, Object>();
		serverInfo.put("os", os);
		serverInfo.put("arch", arch);
		serverInfo.put("version", version);
		return serverInfo;
	}

	/**
	  * 등록된 IP 서버 목록을 가져오는 세모드
	  * @return 등록된 IP 서버 목록
	  */
	public List<Object> getServerList() {
		Map<Object, Object> ipConnect = null;
		List<Object> ipStatus =new ArrayList<Object>();
		String engineInfo = null;

		List<String> ipList = IpListSortByIp.getIP();

		for(String ip:ipList){
			String ipInfo=ip.substring(ip.indexOf("$")+1);
			engineInfo = getEngineVersion(ip);
			ipConnect=new HashMap<Object, Object>();
			ipConnect.put("engine",engineInfo);
			ipConnect.put("result", PropertiesData.ipConnect.get(ipInfo));
			ipConnect.put("server", ipInfo);
			ipStatus.add(ipConnect);
		}
		return ipStatus;
	}

	/**
	  * IP 서버가 가지고 있는 엔진에 대한 버전을 가져오는 메소드
	  * @param ip 엔진 버전을 가지고 올 IP
	  * @return 서버의 엔진 버전
	  */
	public String getEngineVersion(String ip){
		String[] httpIP=ip.split("\\$");
		String url = httpIP[0]+httpIP[1]+":"+PropertiesData.port+"/system/rest/server/engine";
		String result = RestURI.getRequestURL(url);
		String version;
		Map<String, String> engineInfo = new Gson().fromJson(result,Map.class);
		if(engineInfo!=null){
			version = engineInfo.get("EngineVersion");
		}else{
			version = "null";
		}
		return version;
	}


	/**
	  * AVSB1 버전, 라이센스 기간을 가져오는 메소드
	  * @return AVSB1 버전, 라이센스 기간
	  */
	public Map<Object, Object> getAVSBInfo() {
		Map<Object,Object> avsbInfo = new HashMap<Object, Object>();
//		Properties prop = PropertiesData.getProp(FilePath.license);
		String period = PropertiesData.licenseExpire;
		period=period.replace("/","-");
		avsbInfo.put("version", "1.0");
		avsbInfo.put("License", period);
		return avsbInfo;
	}

	/**
	  * Local 서버에서 사용하고 있는 엔진에 대한 정보를 가져오는 메소드
	  * @return Local 서버 엔진 정보
	  */
	public Map<Object, Object> getEngineInfo() {
		Map<Object,Object> engineInfo = new HashMap<Object, Object>();
		String engineVersion = getEngineVersion();
		System.out.println("engineVersion : "+engineVersion);
		if(engineVersion.equals("")){
			engineVersion=null+"["+null+"]";
		}
		engineInfo.put("EngineName", PropertiesData.useEngine);
		engineInfo.put("EngineVersion", engineVersion);
		
		return engineInfo;
	}

	public String getEngineVersion(){
		Properties prop = new Properties();
		String engineVersion = "";
		if(PropertiesData.engine==1){
			V3Scanner.scanFile(FilePath.dummyFile,0,prop);
			engineVersion = prop.getProperty(V3Const.PROP_KEY_TS_VERSION) + "[" + prop.getProperty(V3Const.PROP_KEY_DATE_REV) + "]";
		}else if(PropertiesData.engine==2){
			ViRobotLog.scanFile(FilePath.dummyFile,prop);
			engineVersion = prop.getProperty(ViRobotLog.AVSB_USE_ENGINE_INFO);
		}else if(PropertiesData.engine==3){

		}else if(PropertiesData.engine==4){

		}else{
			engineVersion = null;
		}

		if(prop!=null){
			prop.clear();
		}

		return engineVersion;
	}
}
