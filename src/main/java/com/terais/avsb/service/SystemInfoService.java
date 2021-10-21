package com.terais.avsb.service;

import java.util.List;
import java.util.Map;

public interface SystemInfoService {
	public Map<Object, Object> getServerInfo();
	public List<Object> getServerList();
	public Map<Object, Object> getAVSBInfo();
	public Map<Object, Object> getEngineInfo();
}
