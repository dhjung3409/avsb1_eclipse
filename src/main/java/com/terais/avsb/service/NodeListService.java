package com.terais.avsb.service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
  * @FileName : NodeListService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface NodeListService {
	public Map<String,String> getResultValue(String period);
	public List<String> getDateProp(String period);
	public Properties getProperties(File logFile, Properties pro);
	public int getPeriodInteger(String period);
	public int countMonth();
//	public Map<String,String> getTypeAndIPAddress();
//	public Map<String, String> getSumNode(String period);
//	public List<Map<String,String>> getNodeList();
	public Map<String,Object> getCurrentChart();
//	public Map<String,Object> getReloadChart(String reloadCount);
}