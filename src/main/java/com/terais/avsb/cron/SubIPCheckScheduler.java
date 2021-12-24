package com.terais.avsb.cron;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.RestURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
        logger.info("setSubIPConnect start");
        for(String ip : PropertiesData.subIp){
//            String[] httpIP = ip.split("$");
            getRestResult(ip);
        }
    }

    /**
      * 등록된 IP 정보를 HTTP, HTTPS 와 IP의 두가지 정보로 나누는 메소드
      * @param ip IP 관련 정보를 가지고 있는 문자열
      * @return HTTP 정보와 IP 정보를 분리해 가지고 있는 문자열 배열 ex)[http://, 127.0.0.1]
      */
    public static String[] getHTTPIP(String ip){
        logger.debug("ipcheck: "+ip);
        String[] httpIP = ip.split("\\$");
        return httpIP;
    }

    /**
      * 등록된 IP 서버와 연결되어있는지 REST API 요청으로 확인하는 메소드
      * @param ip 연결 여부를 확인할 IP 정보보
     */
    public static void getRestResult(String ip) {
        boolean status;
        String result = null;
//        RestTemplate rest = null;
        String[] httpIP = ip.split("\\$");
//        if(httpIP[0].equals("https://")){
//            rest = TimeOutRestTemplate.getHttpsRestTemplate();
//        }else if(httpIP[0].equals("http://")){
//            rest = TimeOutRestTemplate.getHttpRestTemplate();
//        }else{
//            logger.error("Incorrectly String: "+ip);
//            PropertiesData.ipConnect.put(ip,false);
//            return;
//        }

        try{
//            rest.getRequestFactory();
            String url = httpIP[0]+httpIP[1]+":"+ PropertiesData.port+"/system/rest/ping";
            logger.debug(url);
//            TimeOutRestTemplate.getRestAPI(url);
            result = RestURI.getRequestURL(url);
//            logger.info("ping result: "+result);
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
}
