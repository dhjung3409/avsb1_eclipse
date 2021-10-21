package com.terais.avsb.module;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestURI {
    private static final Logger logger = LoggerFactory.getLogger(RestURI.class);

    public static String getRequestUri(RestTemplate rest, String uri){
        ResponseEntity<String> response=null;
        String result = null;
        try{
            rest.getRequestFactory();
            response = rest.getForEntity(uri, String.class);
            HttpStatus status = response.getStatusCode();
            if(status.toString().equals("200")&&response.getBody()!=null){
                logger.debug("RestURI if: "+response.getBody());
                result= response.getBody().toString();

            }else if (response.getBody()==null) {
                result="this is null";
            }else{
                logger.debug("RestURI if else");
            }
            logger.debug("RestURI end");
        }catch(Exception e){
            logger.error("Exception : "+e.getMessage());
        }
        logger.debug("result: "+result);
        return result;
    }
}
