package com.terais.avsb.service;

import com.terais.avsb.vo.ScanSchedule;

import java.util.List;
import java.util.Map;

/**
  *
  */
public interface LocalScanSchedulerService {
	public Map<String,Object> checkFile(Map<String,String> data);
	public Map<String, Object> partSelect(Map<String,String> data);
	public int registerScheduler(Map<String, String> path);
	public int registerRepeatScheduler(Map<String,String> data);
	public int checkOverlap(String path, String date, List<ScanSchedule> list);
	public int checkFile(String scheduleFilePath,Map<String,String> data,String date);
	public ScanSchedule setScanSchedule(Map<String,String> data,int no,String date);
	public void deleteScanScheduler(List<String> no,String path, List<ScanSchedule> ss);
	public void deleteResultFile(String fileName);
}
