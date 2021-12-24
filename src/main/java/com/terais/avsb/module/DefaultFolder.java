package com.terais.avsb.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
  * 구동에 필요한 폴더가 없는 경우 생성하는 클래스
  */
public class DefaultFolder {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFolder.class);

	/**
	  * 기본 폴더 생성 메소드
	  */
	public static void makeDefaultFolder(){
		List<File> files = new ArrayList<File>();
		files.add(new File(FilePath.defaultName));
		files.add(new File(FilePath.accountFolder));
		files.add(new File(FilePath.configFolder));
		files.add(new File(FilePath.infoFolder));
		files.add(new File(FilePath.libsFolder));
		files.add(new File(FilePath.logFolder));
		files.add(new File(FilePath.log4jLogFolder));
		files.add(new File(FilePath.tmpFolder));
		for(File file : files){
			logger.debug(file.getPath());
			logger.debug(file.exists()+"");
			if(!file.exists()){
				logger.debug("Create file");
				file.mkdir();
			}
		}
	}
}
