package com.terais.avsb.service;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
  *
  */
public interface ConfigAPIService {
	public void setPropData(Properties prop,String filePath);
	public void setUseAPI(String useAPI);	
	public Set<String> setSubIP(String ip);
	public Set<String> delSubIP(List<String> delItems);
	public void setDateTerm(String rotate);
	public void setReloadTime(String reload);
	public void setCurrentReloadTime(String reload);
	public void setReportCount(String count);

}
