package com.terais.avsb.service;

import java.util.List;

import com.terais.avsb.dto.ReadLog;

public interface PageLogService {
	public List<ReadLog> getPageLog();
	public Object getLogObject(ReadLog readLog);
}
