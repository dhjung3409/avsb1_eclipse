package com.terais.avsb.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.service.ModuleService;
import com.terais.avsb.dto.ResultValue;

/**
  * 통합한 로그 기록을 가지고 오는 클래스
  */
@Service
public class ModuleServiceImpl implements ModuleService{

	private static final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

	/**
	  * 입력된 디렉토리 하위에 있는 로그 기록 정보들을 통합해서 보여주는 메소드
	  * @param fileName 로그 정보들의 상위 디렉토리
	  * @param rv 로그 정보를 담을 ResultValue 객체
	  * @return 기록된 데이터를 통합한 ResultValue 객체
	  */
	public ResultValue getTotal(String fileName, ResultValue rv) {
		// TODO Auto-generated method stub
		File dayDir = new File(fileName);
		Properties p = new Properties();
		
		if(!dayDir.exists()){			
			return rv;
		}else{			
			try{	
				if(dayDir.isDirectory()){
					for(File result:dayDir.listFiles()){
							getTotal(result.getPath(),rv);
					}			
				}else if(dayDir.isFile()){
					if(dayDir.getPath().indexOf("result")!=-1){
						FileInputStream fis = new FileInputStream(dayDir);
						p.load(fis);
						rv.setNormal(rv.getNormal()+Integer.parseInt((String) p.get("normal")));
						rv.setInfected(rv.getInfected()+Integer.parseInt((String) p.get("infected")));
						rv.setDisinfected(rv.getDisinfected()+Integer.parseInt((String) p.get("disinfected")));
						rv.setFailed(rv.getFailed()+Integer.parseInt((String) p.get("failed")));
						rv.setTotal(rv.getTotal()+Integer.parseInt((String) p.get("total")));
						return rv;
					}
				}
			}catch(Exception e){
				logger.error("Get Total Log Exception:" +e.getMessage());
			}
		}
		return rv;
	}
	

}
