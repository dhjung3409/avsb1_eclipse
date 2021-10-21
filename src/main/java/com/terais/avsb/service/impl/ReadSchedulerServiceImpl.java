package com.terais.avsb.service.impl;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.dto.ScanResult;
import com.terais.avsb.dto.ScanResultCount;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.module.LicenseCheck;
import com.terais.avsb.service.ReadSchedulerService;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Service
public class ReadSchedulerServiceImpl implements ReadSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(ReadSchedulerServiceImpl.class);

    public List<ScanSchedule> getSchedulerLines(){
        List<ScanSchedule> scheduler = PathAndConvertGson.convertGson(FilePath.scheduler);
        return scheduler;
    }
    public List<ScanSchedule> getReportLines(){
        List<ScanSchedule> scheduler = PathAndConvertGson.convertGson(FilePath.report);
        logger.debug(scheduler.toString());
        return scheduler;
    }

    public Map<Object,Object> getReportText(String no){
        Map<Object,Object> res = new HashMap<Object, Object>();
        List<ScanSchedule> reports = PathAndConvertGson.convertGson(FilePath.report);
        logger.debug("no: "+no);
        ScanSchedule ss = null;
        for(ScanSchedule report : reports){
            if(report.getNo()==Integer.parseInt(no)){
                logger.debug("report if start");
                ss=report;
                logger.debug("ScanSchedule: "+ss.toString());
                break;
            }
        }
        String reportFilePath = ss.getReport().replace("log","report");
        reportFilePath = FilePath.tmpFolder+"/"+reportFilePath;
        ScanResultCount src = getScanReport(reportFilePath);
        if(src.checkZero()){
            src=null;
        }
        String logFilePath = FilePath.tmpFolder+"/"+ss.getReport();
        logger.debug("ReportPath: "+logFilePath);
        File reportFile = new File(logFilePath);
        if(!reportFile.exists()){
            return null;
        }
        List<ScanResult> sr = PathAndConvertGson.convertGson(logFilePath);
        res.put("report",src);
        res.put("log",sr);
        return res;
    }

    public ScanResultCount getScanReport(String path){
        ScanResultCount src = new ScanResultCount();
        Properties prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream(path);
            prop.load(fis);
            src.setTotal(Integer.parseInt(prop.get("Total").toString()));
            src.setNormal(Integer.parseInt(prop.get("Normal").toString()));
            src.setInfected(Integer.parseInt(prop.get("Infected").toString()));
            src.setCured(Integer.parseInt(prop.get("Cured").toString()));
            src.setFailed(Integer.parseInt(prop.get("Failed").toString()));

        }catch (NumberFormatException e) {
            logger.error("Get Scan Report NumberFormatException: "+e.getMessage());
            setErrorProp(prop, path);
            src = null;
        } catch (NullPointerException e) {
            logger.error("Get Scan Report NullPointerException: "+e.getMessage());
            setErrorProp(prop, path);
            src = null;
        } catch (FileNotFoundException e) {
            logger.error("Get Scan Report FileNotFoundException: "+e.getMessage());
        } catch (IOException e) {
            logger.error("Get Scan Report IOException: "+e.getMessage());
        }

        return src;
    }

    public void setErrorProp(Properties prop, String path){
        prop.setProperty("Total","0");
        prop.setProperty("Normal","0");
        prop.setProperty("Infected","0");
        prop.setProperty("Cured","0");
        prop.setProperty("Failed","0");
        PropertiesData.setProp(prop, path);
        logger.error(path.substring(path.lastIndexOf("/"))+" File Initialization");
    }


}
