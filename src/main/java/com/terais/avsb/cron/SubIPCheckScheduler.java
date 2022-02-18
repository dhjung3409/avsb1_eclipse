package com.terais.avsb.cron;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.module.RestURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
  * 등록된 IP와 연결되어있는지 확인하는 클래스
  */
@Component
public class SubIPCheckScheduler {

    private static final Logger logger = LoggerFactory.getLogger(SubIPCheckScheduler.class);

    /**
     * 라이센스 기간 중 연도의 백의 자리 수
     */
    public static String LI_Y2="0";


    /**
      * 등록된 IP 연결 상태를 확인하는 메소
      */
    public void setSubIPConnect(){
//        logger.info("setSubIPConnect start");
        for(String ip : PropertiesData.subIp){
//            String[] httpIP = ip.split("$");
            getRestResult(ip);
        }
    }

    /**
      * 등록된 IP 정보를 HTTP, HTTPS 와 IP의 두가지 정보로 나누는 메소드
      * @param ip IP 관련 정보를 가지고 있는 문자열
      * @return HTTP 정보와 IP 정보를 분리해 가지고 있는 문자열 배열 ex)["http://", "127.0.0.1"]
      */
    public static String[] getHTTPIP(String ip){
        logger.debug("ipcheck: "+ip);
        String[] httpIP = ip.split("\\$");
        return httpIP;
    }

    /**
      * 등록된 IP 서버와 연결되어있는지 REST API 요청으로 확인하는 메소드
      * @param ip 연결 여부를 확인할 IP 정보
     */
    public static void getRestResult(String ip) {
        boolean status;
        String result = null;
        String[] httpIP = ip.split("\\$");

        try{
            String url = httpIP[0]+httpIP[1]+":"+ PropertiesData.port+"/system/rest/ping";
            logger.debug(url);
            result = RestURI.getRequestURL(url);
            logger.debug("ping result is not null: "+(result.equals("this is null")));
            if(result.equals("this is null")){
                status=true;
            }else{
                status=false;
            }
        } catch(Exception e){
            logger.error("Exception : "+e.getMessage());
            status=false;
        }

        PropertiesData.ipConnect.put(httpIP[1],status);

    }

    public static void searchEnginePath(){
        logger.debug("enginePath Check");
        logger.debug("enginPath boolean: " +PropertiesData.isEnginePath);
        File logFile = new File(FilePath.logFile);
        if(logFile.exists()==true&&PropertiesData.isEnginePath==true){
            return;
        }else {
            logger.info("enginePath Check start");
            Properties prop = null;
            String ahnlab = "";
            String hauri = "";
            String alyac = "";
            String tachyon = "";
            FileInputStream fis = null;
            try {
                prop = new Properties();
                fis = new FileInputStream(FilePath.enginePathFile);
                prop.load(fis);
                ahnlab = prop.getProperty("ahnlab_engine");
                hauri = prop.getProperty("hauri_engine");
                alyac = prop.getProperty("alyac_engine");
                tachyon = prop.getProperty("tachyon_engine");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (prop != null) {
                    prop.clear();
                }
            }

            logger.debug("ahnlab", ahnlab);
            logger.debug("hauri", hauri);
            logger.debug("alyac", alyac);
            logger.debug("tachyon", tachyon);
            File file = null;
            if (ahnlab != null) {
                file = new File(ahnlab);
                FilePath.logPath = file.getParent();
                FilePath.logFile = ahnlab;
            } else if (hauri != null) {
                file = new File(hauri);
                FilePath.logPath = file.getParent();
                FilePath.logFile = hauri;
            } else if (alyac != null) {
                file = new File(alyac);
                FilePath.logPath = file.getParent();
                FilePath.logFile = alyac;
            } else if (tachyon != null) {
                file = new File(tachyon);
                FilePath.logPath = file.getParent();
                FilePath.logFile = tachyon;
            } else {
                FilePath.logPath = FilePath.libsFolder+"/log";
                FilePath.logFile = FilePath.logPath+"/dummy.log";
            }

            logFile = new File(FilePath.logFile);

            if (logFile.exists() == false||(FilePath.logFile.contains(FilePath.libsFolder)&&logFile.length()==0)) {
                PropertiesData.isEnginePath = false;
            } else {
                PropertiesData.isEnginePath = true;
            }

            logger.debug(FilePath.logPath);
            logger.debug(FilePath.logFile);
            logger.debug(String.valueOf(PropertiesData.isEnginePath));
        }
    }
}
