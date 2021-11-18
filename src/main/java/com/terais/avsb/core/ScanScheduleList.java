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

public class ScanScheduleList {

    private static final Logger logger = LoggerFactory.getLogger(ScanScheduleList.class);

    public static String LI_D2="1";

    private static final Type type = new TypeToken<List<ScanSchedule>>() {}.getType();

    public static List<ScanSchedule> scanSchedule = new ArrayList<ScanSchedule>();
    public static List<ScanSchedule> scanReport = new ArrayList<ScanSchedule>();

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
