package com.terais.avsb.service.impl;




import com.google.gson.reflect.TypeToken;
import com.terais.avsb.cron.SubIPCheckSchduler;
import com.terais.avsb.dto.Current;
import com.terais.avsb.module.IpListSortByIp;
import com.terais.avsb.module.RestURI;
import com.terais.avsb.service.NodeAndCurrentGetService;
import com.terais.avsb.vo.CurrentLogVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.RegularExpression;
import com.terais.avsb.core.TimeOutRestTemplate;

import java.lang.reflect.Type;
import java.util.*;

@Service
public class NodeAndCurrentGetServiceImpl implements NodeAndCurrentGetService {

	private static final Logger logger = LoggerFactory.getLogger(NodeAndCurrentGetServiceImpl.class);

	private static final Type type = new TypeToken<List<CurrentLogVO>>() {}.getType();

	public String getRest(String ip, String select, String option){
		logger.debug("get Rest String");
		logger.debug("ip: "+ip);
		String[] httpIP= SubIPCheckSchduler.getHTTPIP(ip);
		if(!RegularExpression.checkIP(httpIP[1])){
			logger.debug("Not IP");
			return null;
		}
		if(!PropertiesData.ipConnect.get(httpIP[1])){
			logger.debug("IP Not Connect");
			return null;
		}
		logger.debug("IP");
		String result = null;
		try{
			RestTemplate rest = null;
			if(httpIP[0].equals("https://")){
				rest = TimeOutRestTemplate.getHttpsRestTemplate();
			}else{
				rest = TimeOutRestTemplate.getHttpRestTemplate();
			}

			String url = httpIP[0]+httpIP[1]+":"+PropertiesData.port+"/"+select+"/rest/"+option;
			result = RestURI.getRequestUri(rest,url);
		}catch(Exception e){
			logger.error("getRest: "+e.getMessage());
			result =  null;
		}
		return result;
	}

	public Map<String,Object> getLastLine(){
		Map<String,Object> map = new HashMap<String, Object>();
		List<CurrentLogVO> currentLog = new ArrayList<CurrentLogVO>();
		map.put("reload",PropertiesData.logReloadTime);
		for(String ip : PropertiesData.subIp){
			String ipInfo=ip.substring(ip.indexOf("$")+1);
			String json = getRest(ip,"dashboard","last/log?ip="+ipInfo);
			if(json==null){continue;}
			List<CurrentLogVO> list = castGson(json);
			currentLog.addAll(list);
		}
		Collections.sort(currentLog,new CurrentLogVO());
		while(currentLog.size()>50){
			currentLog.remove(currentLog.size()-1);
		}
		map.put("currentLog",currentLog);
		return map;
	}
	public List<CurrentLogVO> castGson(String json){
		return new Gson().fromJson(json,type);
	}

	public Map<Object,Object> getNode(String period){
		Map<Object,Object> node = new HashMap<Object, Object>();
		List<Object> infected = new ArrayList<Object>();
		List<Object> normal = new ArrayList<Object>();
		List<Object> disinfected = new ArrayList<Object>();
		List<Object> failed = new ArrayList<Object>();
		List<Object> ips = new ArrayList<Object>();
		String total = "0";

		List<String> ipList = new ArrayList<String>();
		for(String ip : PropertiesData.subIp){
			ipList.add(ip);
		}

		Collections.sort(ipList, new IpListSortByIp());

		for(String ip : ipList){
			logger.info("getNodeRest start");
			String ipInfo=ip.substring(ip.indexOf("$")+1);
			String json = PropertiesData.ipConnect.get(ipInfo)?getRest(ip,"dashboard","node?period="+period):null;
			logger.info("node rest json: "+json);
			if(json==null){
				continue;
			}
			Map<?,?> map = new Gson().fromJson(json, Map.class);
			infected.add(map.get("infected"));
			normal.add(map.get("normal"));
			disinfected.add(map.get("disinfected"));
			failed.add(map.get("failed"));
			ips.add(ipInfo);
			if(Integer.parseInt(total)<Integer.parseInt(map.get("total").toString())){
				total = map.get("total").toString();
			}
		}

		node.put("ip",ips);
		node.put("Infect",infected);
		node.put("Normal",normal);
		node.put("Cured",disinfected);
		node.put("Fail",failed);
		node.put("total",total);
		node.put("interval",PropertiesData.resReloadTime);
		logger.debug("List Count: "+node.toString()+"");
		return node;
	}

	public List<Object> getChart(String option,String currentReload){
		List<Object> node = new ArrayList<Object>();
		String json=null;

		List<String> ipList = IpListSortByIp.getIP();

		for(String ip : ipList){
			logger.debug("getRest start");
			String ipInfo=ip.substring(ip.indexOf("$")+1);
			if(currentReload.equals("0")) {
				json = PropertiesData.ipConnect.get(ipInfo)?getRest(ip, "dashboard", option):null;
			}else{
				json = PropertiesData.ipConnect.get(ipInfo)?getRest(ip, "dashboard", option+"?reloadCount="+PropertiesData.currentReloadTime):null;
			}
			if(json==null){
				continue;
			}
			Current current = new Gson().fromJson(json, Current.class);
			logger.debug(current.toString());

			current.setIp(ipInfo);

			node.add(current);

		}
		logger.debug("List Count: "+node.toString()+"");
		node.add(PropertiesData.currentReloadTime);
		return node;
	}


}
