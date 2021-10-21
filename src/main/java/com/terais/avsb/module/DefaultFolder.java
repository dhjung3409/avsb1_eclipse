package com.terais.avsb.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DefaultFolder {

	private static final Logger logger = LoggerFactory.getLogger(DefaultFolder.class);

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
