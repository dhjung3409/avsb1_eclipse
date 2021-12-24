package com.terais.avsb.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.vo.ScanSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
  * 스케줄러 데이터, 리포트 데이터를 불러와 보유하고 있는 클래스
  */
public class ScanScheduleList {

    private static final Logger logger = LoggerFactory.getLogger(ScanScheduleList.class);

    /**
     * 라이센스 기간 중 일자의 일의 자리 수
     */
    public static String LI_D2="1";

    /**
     * List<ScanSchedule>의 TypeToken
     */
    private static final Type type = new TypeToken<List<ScanSchedule>>() {}.getType();

    /**
     * 스케줄러 데이터가 저장된 리스트
     */
    public static List<ScanSchedule> scanSchedule = new ArrayList<ScanSchedule>();
    
    /**
     * 리포트 데이터가 저장된 리스트
     */
    public static List<ScanSchedule> scanReport = new ArrayList<ScanSchedule>();

    /**
      * 파일에 저장된 스케줄러, 리포트 정보가 존재 할 경우 불러오고, 존재하지 않을 경우 생성하는 메소드
      */
    public static void initScanInfo(){
        File scheduleFile = new File(FilePath.scheduler);
        File reportFile = new File(FilePath.report);

        try {
            if(!scheduleFile.exists()){
                createFile(scheduleFile,PathAndConvertGson.gson,scanSchedule);
            }
            if(!reportFile.exists()){
                createFile(reportFile,PathAndConvertGson.gson,scanReport);
            }
            scanSchedule = openJsonFile(scheduleFile.getCanonicalPath());
            if(scanSchedule==null){
                scanSchedule = new ArrayList<ScanSchedule>();
            }
            scanReport = openJsonFile(reportFile.getCanonicalPath());
            if(scanReport==null){
                scanReport = new ArrayList<ScanSchedule>();
            }


        } catch (IOException e) {
            logger.error("ScanScheduleList InitScanInfo IOException: "+e.getMessage());
        }

    }

    /**
      * 스케줄러 혹은 리포트 데이터가 저장된 파일을 생성하는 메소드
      * @param file 생성할 파일 객체
      * @param gson JSON 데이터를 저장하는데 사용 할 Gson 객체
      * @param list 저장할 데이터가 담긴 리스트
      */
    public static void createFile(File file, Gson gson,List<ScanSchedule> list){
        FileWriter writer = null;
        try {
            file.createNewFile();
            writer = new FileWriter(file);
            gson.toJson(list, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.error("ScanScheduleList createFile IOException: "+e.getMessage());
        }finally{
            if(writer!=null){
                writer=null;
            }
        }
    }

    /**
      * JSON 파일에 저장된 데이터를 불러오는 메소드
      * @param path 불러올 JSON 파일의 경로
      * @return JSON 데이터가 저장된 리스트
      */
    public static <T> List<T> openJsonFile(String path){
        File file = new File(path);
        List<T> list = null;
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            list=new Gson().fromJson(jsonReader, type);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            logger.error("ScanScheduler openJsonFile FileNotFoundException: "+e.getMessage());
            list=null;
        } catch (Exception e){
            logger.error("ScanScheduler openJsonFile Exception: "+e.getMessage());
        }
        return list;
    }


}
