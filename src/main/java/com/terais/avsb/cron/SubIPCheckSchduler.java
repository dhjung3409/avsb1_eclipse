package com.terais.avsb.cron;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.TimeOutRestTemplate;
import com.terais.avsb.module.RestURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.NoRouteToHostException;

@Component
public class SubIPCheckSchduler {

    private static final Logger logger = LoggerFactory.getLogger(SubIPCheckSchduler.class);

    public static String LI_Y2="0";




    public void setSubIPConnect(){
        logger.debug("setSubIPConnect start");
        for(String ip : PropertiesData.subIp){
//            String[] httpIP = ip.split("$");
            getRestResult(ip);
        }
    }

    public static String[] getHTTPIP(String ip){
        logger.info("ipcheck: "+ip);
        String[] httpIP = ip.split("\\$");
        for(String ipInfo:httpIP){
            logger.info("httpIP check: "+ipInfo);
        }

        return httpIP;
    }

    public static void getRestResult(String ip) {
        boolean status;
        String result = null;
        RestTemplate rest = null;
        String[] httpIP = ip.split("\\$");
        if(httpIP[0].equals("https://")){
            rest = TimeOutRestTemplate.getHttpsRestTemplate();
        }else if(httpIP[0].equals("http://")){
            rest = TimeOutRestTemplate.getHttpRestTemplate();
        }else{
            logger.error("Incorrectly String: "+ip);
            PropertiesData.ipConnect.put(ip,false);
            return;
        }

        try{
            rest.getRequestFactory();
            String url = httpIP[0]+httpIP[1]+":"+ PropertiesData.port+"/system/rest/ping";
            logger.debug(url);
            result = RestURI.getRequestUri(rest,url);
            if(result!=null){
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
