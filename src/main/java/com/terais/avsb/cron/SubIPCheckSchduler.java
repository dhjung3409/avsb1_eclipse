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


    public void setSubIPConnect(){
        logger.debug("setSubIPConnect start");
        for(String ip : PropertiesData.subIp){
            getRestResult(ip);
        }
    }

    public static void getRestResult(String ip) {
        boolean status=false;
        String result = null;
        RestTemplate rest= TimeOutRestTemplate.getRestTemplate();
        try{
            rest.getRequestFactory();
            String url = "http://"+ip+":"+ PropertiesData.port+"/system/rest/ping";
            logger.debug(url);
            result = RestURI.getRequestUri(rest,url);
            if(result!=null){
                status=true;
            }else{

            }
        } catch(Exception e){
            logger.error("Exception : "+e.getMessage());
            status=false;
        }

        PropertiesData.ipConnect.put(ip,status);

    }
}
