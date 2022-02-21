package com.terais.avsb.service.impl;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.dto.ScanResult;
import com.terais.avsb.dto.ScanResultCount;
import com.terais.avsb.module.CheckOS;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.ReadSchedulerService;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
  * 스케줄러 검사 관련 데이터들을 다루는 클래스
  */
@Service
public class ReadSchedulerServiceImpl implements ReadSchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(ReadSchedulerServiceImpl.class);

    /**
      * 스케줄러 검사 목록을 가져오는 메소드
      * @return 스케줄러 검사 목록
      */
    public List<ScanSchedule> getSchedulerLines(){
        List<ScanSchedule> scheduler = PathAndConvertGson.convertGson(FilePath.scheduler);
        return scheduler;
    }
    
    /**
      * 스케줄러 검사 결과 목록을 가져오는 메소드
      * @return 스케줄러 검사 결과 목록
      */
    public List<ScanSchedule> getReportLines(){
        List<ScanSchedule> scheduler = PathAndConvertGson.convertGson(FilePath.report);
        logger.debug(scheduler.toString());
        return scheduler;
    }

    /**
      * 스케줄러 검사 결과 데이터를 가져오는 메소드
      * @param no 스케줄러 검사 결과 고유 번호
      * @return 스케줄러 검사 결과 result, log 데이터
      */
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
        reportFilePath = FilePath.tmpFolder+ CheckOS.osSeparator+reportFilePath;
        ScanResultCount src = getScanReport(reportFilePath);
        if(src.checkZero()){
            src=null;
        }
        String logFilePath = FilePath.tmpFolder+CheckOS.osSeparator+ss.getReport();
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

    /**
      * 스케줄러 검사 결과 report 파일의 데이터를 가져오는 메소드
      * @param path report 파일의 경로
      * @return report 파일에 저장된 데이터
      */
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

    /**
      * report 파일 읽어오기에 문제가 생겼을 경우 기본 값으로 잡아주는 메소드
      * @param prop report 파일의 Properties 데이터
      * @param path report 파일의 경로
      */
    public void setErrorProp(Properties prop, String path){
        prop.setProperty("Total","0");
        prop.setProperty("Normal","0");
        prop.setProperty("Infected","0");
        prop.setProperty("Cured","0");
        prop.setProperty("Failed","0");
        PropertiesData.setProp(prop, path);
        logger.error(path.substring(path.lastIndexOf(CheckOS.osSeparator))+" File Initialization");
    }


}
