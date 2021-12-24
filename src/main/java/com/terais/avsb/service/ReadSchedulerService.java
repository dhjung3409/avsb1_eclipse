package com.terais.avsb.service;

import com.terais.avsb.dto.ScanResultCount;
import com.terais.avsb.vo.ScanSchedule;

import java.util.List;
import java.util.Map;

/**
  * @FileName : ReadSchedulerService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface ReadSchedulerService {
    public List<ScanSchedule> getSchedulerLines();
    public List<ScanSchedule> getReportLines();
    public Map<Object,Object> getReportText(String no);
    public ScanResultCount getScanReport(String path);
}
