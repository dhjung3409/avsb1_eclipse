package com.terais.avsb.service;

import java.util.List;
import java.util.Map;

/**
  * @FileName : SystemInfoService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface SystemInfoService {
	public Map<Object, Object> getServerInfo();
	public List<Object> getServerList();
	public Map<Object, Object> getAVSBInfo();
	public Map<Object, Object> getEngineInfo();
}
