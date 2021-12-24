package com.terais.avsb.module;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.terais.avsb.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ahnlab.v3engine.V3Scanner;
import com.terais.avsb.cron.CurrentCountScheduler;
import com.terais.avsb.cron.LogReadScheduler;


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
		logger.info("java version: "+System.getProperty("java.version"));
		LogReadScheduler logRead=null;
		logger.info("server started!");
		System.out.println(System.getProperty("user.dir"));
		DefaultFolder.makeDefaultFolder();

		ReadLogPath.readLogPath();
		LicenseCheck lc = new LicenseCheck();
		logger.debug("license status: "+ PropertiesData.licenseStatus);
		LicenseCheck.checkPeriod();

		CheckOS.osCheck();
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
		if(PropertiesData.licenseStatus) {
			logRead.readLog();
		}
		CurrentCountScheduler.initList();
		logger.debug("Properties Path:" + FilePath.v3properties);
		V3Scanner.setConfPropertiesPath(FilePath.v3properties);

	}


	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	public void contextInitialized(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		init();
	}
	
}