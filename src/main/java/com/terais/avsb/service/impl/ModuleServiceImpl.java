package com.terais.avsb.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.service.ModuleService;
import com.terais.avsb.dto.ResultValue;

@Service
public class ModuleServiceImpl implements ModuleService{

	private static final Logger logger = LoggerFactory.getLogger(ModuleServiceImpl.class);

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
