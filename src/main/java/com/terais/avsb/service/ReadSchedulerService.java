package com.terais.avsb.service;

import com.terais.avsb.dto.ScanResultCount;
import com.terais.avsb.vo.ScanSchedule;

import java.util.List;
import java.util.Map;

public interface ReadSchedulerService {
    public List<ScanSchedule> getSchedulerLines();
    public List<ScanSchedule> getReportLines();
    public Map<Object,Object> getReportText(String no);
    public ScanResultCount getScanReport(String path);
}
