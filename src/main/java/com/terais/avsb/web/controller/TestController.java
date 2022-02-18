package com.terais.avsb.web.controller;

import com.terais.avsb.service.impl.HauriScanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("test/rest")
public class TestController {

    @Autowired
    HauriScanServiceImpl hauriScanService;

    @RequestMapping(value = "/scan",method = RequestMethod.GET)
    @ResponseBody
    public String  scanFileViRobot(@RequestParam String path){
        hauriScanService.scanVrSDK(path);
        System.out.println("test end");
//        ViRobotLog.scanFile(path);
        return "scan end";
    }
}
