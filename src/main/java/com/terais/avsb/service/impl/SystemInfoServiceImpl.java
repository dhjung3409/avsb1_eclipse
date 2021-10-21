package com.terais.avsb.service.impl;


import java.util.*;

import com.google.gson.Gson;
import com.terais.avsb.module.RestURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ahnlab.v3engine.V3Const;
import com.ahnlab.v3engine.V3Scanner;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.TimeOutRestTemplate;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.SystemInfoService;

@Service
public class SystemInfoServiceImpl implements SystemInfoService {


	private static final Logger logger = LoggerFactory.getLogger(SystemInfoServiceImpl.class);

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

	public List<Object> getServerList() {
		Set<String> ips = PropertiesData.subIp;
		RestTemplate rest= TimeOutRestTemplate.getRestTemplate();
		Map<Object, Object> ipConnect = null;
		List<Object> ipStatus =new ArrayList<Object>();
		String engineInfo = null;
		for(String ip:ips){
			engineInfo = getEngineVersion(ip,rest);
			ipConnect=new HashMap<Object, Object>();
			ipConnect.put("engine",engineInfo);
			ipConnect.put("result", PropertiesData.ipConnect.get(ip));
			ipConnect.put("server", ip);
			ipStatus.add(ipConnect);
		}
		return ipStatus;
	}

	public String getEngineVersion(String ip,RestTemplate rest){
		String url = "http://"+ip+":"+PropertiesData.port+"/system/rest/server/engine";
		String result = RestURI.getRequestUri(rest,url);
		String version;
		Map<String, String> engineInfo = new Gson().fromJson(result,Map.class);
		if(engineInfo!=null){
			version = engineInfo.get("EngineVersion");
		}else{
			version = "null";
		}
		return version;
	}


	public Map<Object, Object> getAVSBInfo() {
		Map<Object,Object> avsbInfo = new HashMap<Object, Object>();
		Properties prop = PropertiesData.getProp(FilePath.license);
		String period = prop.get("expired").toString();
		avsbInfo.put("version", "1.0");
		avsbInfo.put("License", period);
		return avsbInfo;
	}

	public Map<Object, Object> getEngineInfo() {
		Map<Object,Object> engineInfo = new HashMap<Object, Object>();
		Properties prop = new Properties();
		V3Scanner.scanFile(FilePath.dummyFile,0,prop);
		String engineVersion = prop.getProperty(V3Const.PROP_KEY_TS_VERSION)+"["+prop.getProperty(V3Const.PROP_KEY_DATE_REV)+"]";
		engineInfo.put("EngineName", PropertiesData.useEngine);
		engineInfo.put("EngineVersion", engineVersion);
		
		return engineInfo;
	}
}
