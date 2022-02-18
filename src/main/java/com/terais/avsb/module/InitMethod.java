package com.terais.avsb.module;

import com.ahnlab.v3engine.V3Scanner;
import com.terais.avsb.core.CurrentLog;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.ScanScheduleList;
import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.cron.LogReadScheduler;
import com.terais.avsb.lib.ViRobotLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;


/**
  * AVSB 시작 초기화 구동 클래스
  */
public class InitMethod implements ServletContextListener{

	private static final Logger logger = LoggerFactory.getLogger(InitMethod.class);
	
	public InitMethod(){
		
	}

	/**
	  * AVSB 초기화 동작 메소드
	  */
	public void init(){
		CheckOS.osCheck();
		logger.info("java version: "+System.getProperty("java.version"));
		LogReadScheduler logRead=null;
		logger.info("server started!");
		System.out.println(System.getProperty("user.dir"));
		DefaultFolder.makeDefaultFolder();
		ReadLogPath.readLogPath();
		LicenseCheck lc = new LicenseCheck();
		logger.debug("license status: "+ PropertiesData.licenseStatus);
		LicenseCheck.checkPeriod();


		File configFile = new File(FilePath.configFile);
		File logFolder = new File(FilePath.logPath);
		File logFile = new File(FilePath.logFile);
		File classPathFile = new File(FilePath.IpConfigFile);
		logger.debug(classPathFile.getPath());
		CurrentLog.getCurrentLog(logFolder);
		ScanScheduleList.initScanInfo();
		DefaultAccount.createDefaultAccount();
		DefaultConfigProperties.createConfigFile(configFile);
		DefaultConfigProperties.createIpConfigFile(classPathFile);
		DefaultConfigProperties.createTmpSeqProperties();
		ReadLogPath.checkLogPath(logFolder, logFile);
		logRead = new LogReadScheduler();
		logRead.nowSetDateFile();
		logRead.makeResultDirectory();
		logRead.initLogElement();
		logRead.setResultMap();
		if(PropertiesData.licenseStatus) {
			logRead.readLog();
		}
		CurrentCountScheduler.initList();

		if(PropertiesData.engine==1) {
			logger.debug("Properties Path:" + FilePath.v3properties);
			V3Scanner.setConfPropertiesPath(FilePath.v3properties);
		}else if(PropertiesData.engine==2){
			logger.info("Properties Path : "+FilePath.vrsdkProperties);
			ViRobotLog.setConfigPropertiesPath(FilePath.vrsdkProperties);
			logger.info("Properties setting");
		}
		logger.info("setting if end");

	}


	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		init();
	}
	
}