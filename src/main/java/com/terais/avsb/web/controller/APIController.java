package com.terais.avsb.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.terais.avsb.service.impl.APIServiceImpl;
import com.terais.avsb.vo.APILog;

@Controller
@RequestMapping(value = "/api")
public class APIController {

    private static final Logger logger = LoggerFactory.getLogger(APIController.class);

    @Autowired
    private APIServiceImpl apiService;

    @RequestMapping(value = "log",method=RequestMethod.POST)
    public void insertLog(@RequestParam APILog apiLog){
        String ip = null;
        try{
            HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            ip = req.getHeader("X-FORWARDED-FOR");

            if (ip == null){
                ip = (req.getRemoteAddr()).trim();
            }else{
                ip = "127.0.0.1";
            }
            logger.debug(apiLog.toString());
            apiLog.setClient(ip);
            apiService.insertLog(apiLog);

        }catch (NullPointerException err){
            logger.error("apiLog : "+apiLog.toString());
        }catch (NumberFormatException err){
            ip = "127.0.0.1";
            logger.error("apiLog NumberFormat Error : "+apiLog.toString());
        }catch (Exception err){
            ip = "127.0.0.1";
            logger.error("apiLog Exception Error : "+apiLog.toString());
        }

    }

}
