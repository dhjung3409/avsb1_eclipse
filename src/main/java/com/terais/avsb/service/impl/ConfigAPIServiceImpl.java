package com.terais.avsb.service.impl;

import java.io.*;
import java.security.Principal;
import java.util.*;

import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.SimpleDateFormatCore;
import com.terais.avsb.cron.SubIPCheckSchduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.ConfigAPIService;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


@Service
public class ConfigAPIServiceImpl implements ConfigAPIService{

	private static final Logger logger = LoggerFactory.getLogger(ConfigAPIServiceImpl.class);

	private String sendFileIP = "";

	public void setPropData(Properties prop,String filePath){
		FileOutputStream fos = null;
		File file = new File(filePath);
		try {
			if (!filePath.contains("ip")) {
				prop.setProperty("date_term", PropertiesData.dateTerm);
				prop.setProperty("use_api", PropertiesData.useApi);
				prop.setProperty("port", PropertiesData.port);
				prop.setProperty("install_path", PropertiesData.installPath);
				prop.setProperty("install_day", PropertiesData.installDate);
				prop.setProperty("report_count",PropertiesData.reportCount);
				prop.setProperty("res_reload_time", PropertiesData.resReloadTime);
				prop.setProperty("current_reload_time", PropertiesData.currentReloadTime);
				prop.setProperty("log_reload_time", PropertiesData.logReloadTime);
			}
			fos=new FileOutputStream(file);
			prop.store(fos, filePath);
		} catch (FileNotFoundException e) {
			logger.error("Set Properties Data FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("Set Properties Data IOException: "+e.getMessage());
		}finally{
			try {
				fos.close();
				prop.clear();
			} catch (IOException e) {
				logger.error("Set Properties Data OutputStream IOException: "+e.getMessage());
			}
		}
	}

	public void setUseAPI(String useAPI) {
		Properties prop = new Properties();
		PropertiesData.useApi=useAPI;
		setPropData(prop, FilePath.configFile);
		logger.debug("useAPI Change");
	}

	public Set<String> setSubIP(String ip) {
		Properties prop = new Properties();		
		PropertiesData.subIp.add(ip);
		String subIp = PropertiesData.subIp.toString();
		subIp=subIp.replace("[", "");
		subIp=subIp.replace("]", "");
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		SubIPCheckSchduler.getRestResult(ip);
		return PropertiesData.subIp;
	}

	public Set<String> delSubIP(List<String> delItems){
		Properties prop = new Properties();		
		Set<String> subIpList = PropertiesData.subIp;
		int defaultIp = delItems.indexOf("127.0.0.1");

		if(defaultIp!=-1){
			logger.error("IP 127.0.0.1 can not delete");
			delItems.remove(defaultIp);
		}

		for(String delItem : delItems){
			subIpList.remove(delItem);
		}
		String subIp = subIpList.toString();
		PropertiesData.subIp=subIpList;
		subIp=PathAndConvertGson.modListString(subIp);
		prop.setProperty("sub_ip", subIp);
		setPropData(prop, FilePath.IpConfigFile);
		return subIpList;
	}



	public void setDateTerm(String rotate) {
		Properties prop = new Properties();
		PropertiesData.dateTerm=rotate;
		setPropData(prop, FilePath.configFile);		
	}
	
	public void setReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.resReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}
	
	public void setCurrentReloadTime(String reload) {
		Properties prop = new Properties();
		PropertiesData.currentReloadTime=reload;
		setPropData(prop, FilePath.configFile);		
	}

	public void setReportCount(String count){
		Properties prop = new Properties();
		PropertiesData.reportCount=count;
		setPropData(prop,FilePath.configFile);
	}
	public void setLogReloadTime(String reload){
		Properties prop = new Properties();
		PropertiesData.logReloadTime=reload;
		setPropData(prop,FilePath.configFile);
	}

	public Map<String, List<Object>> getUpdateFile(){
		Map<String, List<Object>> map = new HashMap<String, List<Object>>();
		map.put("fileSize",PropertiesData.updateFileSize);
		map.put("fileName",PropertiesData.updateFileNames);
		map.put("fileDate",PropertiesData.updateFileDate);
		return map;
	}


	public boolean saveFileInfo(String fileName, long fileSize, Principal principal){
		logger.info("inputData: "+fileName);
		logger.info("inputData: "+fileSize);
		logger.info("osName: "+PropertiesData.osName);
		boolean res = true;

		if(principal.getName()==null){
			res=false;
		}else if((fileName.contains("v3exp")||fileName.contains("engine"))&&(fileName.contains("tar.Z")==false&&fileName.contains("tar")==false)){
			res=false;
		}else if((fileName.contains("v3exp")==false&&fileName.contains("engine")==false)||fileName.contains(PropertiesData.osName)==false){
			res=false;
		}else {
			while (PropertiesData.updateFileSize.size() >= 2) {
				PropertiesData.updateFileSize.remove(1);
			}

			PropertiesData.updateFileSize.add(0, fileSize);

			while (PropertiesData.updateFileNames.size() >= 2) {
				PropertiesData.updateFileNames.remove(1);
			}

			PropertiesData.updateFileNames.add(0, fileName);

			while (PropertiesData.updateFileDate.size() >= 2) {
				PropertiesData.updateFileDate.remove(1);
			}

			PropertiesData.updateFileDate.add(0, SimpleDateFormatCore.sdf.format(new Date()));

			String propFileSize = PropertiesData.updateFileSize.toString();
			propFileSize = PathAndConvertGson.modListString(propFileSize);
			String propFileName = PropertiesData.updateFileNames.toString();
			propFileName = PathAndConvertGson.modListString(propFileName);
			String propFileDate = PropertiesData.updateFileDate.toString();
			propFileDate = PathAndConvertGson.modListString(propFileDate);
			Properties prop = PropertiesData.getProp(FilePath.uploadFileInfo);
			if (prop.isEmpty()) {
				prop = new Properties();
			}
			prop.setProperty("file_size", propFileSize);
			prop.setProperty("file_name", propFileName);
			prop.setProperty("file_date", propFileDate);
			PropertiesData.setProp(prop, FilePath.uploadFileInfo);
		}
		return res;
	}

	public int fileTest(MultipartHttpServletRequest req){
		int res = 0;
		logger.info("upload start");
		File dir = new File(FilePath.uploadFolder);
		if(!dir.exists()){
			dir.mkdir();
		}
		Iterator<String> iterator = req.getFileNames();

		String uploadFileName;
		MultipartFile mFile = null;
		String orgFileName = "";

		while(iterator.hasNext()){
			uploadFileName=iterator.next();

			mFile=req.getFile(uploadFileName);

			orgFileName = mFile.getOriginalFilename();
			String command;
			if(orgFileName != null && orgFileName.length() != 0) {
				File uploadFile = null;

				try {
					FilePath.uploadFile = FilePath.uploadFolder+"/"+orgFileName;
					uploadFile = new File(FilePath.uploadFile);
					logger.info("file uploading");

					mFile.transferTo(uploadFile);
					saveUploadFile(orgFileName);
					logger.info("upload File Size"+mFile.getSize());
					logger.info("upload File Size"+PropertiesData.updateFileSize.toString());
					logger.info("upload File Name"+mFile.getOriginalFilename());
					long fileSize = PropertiesData.updateFileSize.get(0)!=null?Long.parseLong(String.valueOf(PropertiesData.updateFileSize.get(0))):0;
					if(mFile.getSize()!=fileSize){
						uploadFile.delete();
						return res;
					}else {
						res = 1;
					}
				}catch(Exception e){
					logger.error("File Upload Error");
					uploadFile.delete();
				}
			}
		}
		logger.info("upload end");
		return res;
	}

	public void saveUploadFile(String fileName){

		String path_tmp;
		File extractFolder = new File(FilePath.uploadExtractFolder);
		if(fileName.contains(".tar.Z")){
			String gun = "gunzip "+FilePath.uploadFile;
			String [] gunzipCommand = {"/bin/sh","-c",gun};
			actionCommand(gunzipCommand);
			FilePath.uploadFile=FilePath.uploadFile.replace(".Z","").trim();
		}
		if(!extractFolder.exists()){
			extractFolder.mkdir();
		}
		if(PropertiesData.osName.contains("sol")||PropertiesData.osName.contains("sun")){
			String  uploadFile = FilePath.uploadFile.substring(FilePath.uploadFile.lastIndexOf("/")+1);
			path_tmp = "cp " + FilePath.uploadFile + " " + FilePath.uploadExtractFolder
			+ "&& cd "+FilePath.uploadExtractFolder+" && tar -xf "+uploadFile+" && rm -rf "+uploadFile;
		}else {
			path_tmp = "tar -xf " + FilePath.uploadFile + " -C " + FilePath.uploadExtractFolder;
		}
		String [] zipCommand = {"/bin/sh","-c",path_tmp};
		actionCommand(zipCommand);
		File uploadFile = new File(FilePath.uploadFileSavePath);
		File secondFile = new File(FilePath.secondFileSavePath);
		if(uploadFile.exists()&&secondFile.exists()){
			secondFile.delete();
		}

		if(uploadFile.exists()){
			String command = "mv "+FilePath.uploadFileSavePath+" "+FilePath.secondFileSavePath;
			String [] mvCommand = {"/bin/sh","-c",command};
			actionCommand(mvCommand);
		}
		String tarZip = "tar -cf "+FilePath.uploadFileSavePath+" -C "+FilePath.uploadExtractFolder+" ./";
		actionCommand(new String[]{"/bin/sh", "-c", tarZip});
		File file = new File(FilePath.uploadFile);
		if(file.exists()){
			file.delete();
		}
	}

	public List<Object> updateEngine(int num){

		List<Object> res = new ArrayList<Object>();
		String filePath;
		if(num==-1) {
			res.add(-1);
			return res;
		}else if(num==0){
			filePath = FilePath.uploadFileSavePath;
		}else{
			filePath = FilePath.secondFileSavePath;
		}
		String path_tmp = "";
		if(PropertiesData.osName.contains("sol")||PropertiesData.osName.contains("sun")){
			String  uploadFile = filePath.substring(filePath.lastIndexOf("/")+1);
			path_tmp = "cp " + filePath + " " + FilePath.extractFolder
					+ "&& cd "+FilePath.extractFolder+" && tar -xf "+uploadFile+" && rm -rf "+uploadFile;
		}else{
			path_tmp = "tar -xf "+filePath+" -C "+FilePath.extractFolder;
		}
		String[] zip = {"/bin/sh","-c",path_tmp} ;

		File file = new File(FilePath.extractFolder);
		if(!file.exists()){
			file.mkdir();
		}
		logger.info(zip.toString());
		actionCommand(zip);
		for(File name : file.listFiles()) {
			logger.info("fileList: "+name.getName());
		}

		res.add(1);
		logger.info("Runtime end");
		copyFile(FilePath.v3option,FilePath.copyV3option);
		copyFile(FilePath.copyV3option,FilePath.v3option);
		res.add(2);
		String engineRestart = FilePath.v3File+" restart";
		String [] restartCommand = {"/bin/sh","-c",engineRestart};
		actionCommand(restartCommand);
		String update = FilePath.v3File+" update";
		String [] updateCommand = {"/bin/sh","-c",update};
		res.addAll(actionCommand(updateCommand));
		res.add(3);



		File optionFile = new File(FilePath.v3option);
		if(optionFile.exists()){
			optionFile.delete();
		}

		String mv = "mv "+FilePath.copyV3option+" "+FilePath.v3option;
		String [] mvCommand = {"/bin/sh","-c",mv};
		actionCommand(mvCommand);

		String deleteExtractFile = "rm "+FilePath.extractFolder+"/*";
		String [] deleteCommand = {"/bin/sh","-c",deleteExtractFile};
		actionCommand(deleteCommand);
		return res;


	}

	public List<String> actionCommand(String [] command){

		for(String a : command){
			logger.info(a);
		}

		Process p=null;
		List<String> list = new ArrayList<String>();
		BufferedReader bfr = null;
		try {

			p= Runtime.getRuntime().exec(command);
			bfr = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = bfr.readLine()) != null) {
				logger.info(line);
				if(list.size()>0&&list.get(list.size()-1).equals(line)){

				}else if(line==null||line.length()==0){

				}else{
					list.add(line);
				}

				if(line.contains("Engine is successfully initialized.")){
					break;
				}
			}


			if (bfr != null) {
				bfr.close();
			}


		} catch (IOException e) {
			e.printStackTrace();
					}
		return list;

	}
	public void copyFile(String origin,String copy){
		File option = new File(origin);
		File copyFile = new File(copy);
		BufferedReader bfr = null;
		String line = null;
		FileWriter fw = null;
		try {
			if(copyFile.exists()){
				copyFile.delete();
			}
			bfr = new BufferedReader(new FileReader(option));
			fw = new FileWriter(copy);
			while((line= bfr.readLine())!=null){
				if(line.contains("downloaded_engine=")||line.contains("downloader_path=none")){
					continue;
				}
				fw.write(line+"\n");
			}
			if(copy.contains("/option.cfg")){
				fw.write("downloaded_engine="+FilePath.extractFolder+"\n");
				fw.write("downloader_path=none"+"\n");
			}
			fw.flush();
			fw.close();
		} catch (FileNotFoundException e) {
			logger.error("Option Copy Error FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			logger.error("Option Copy Error IOException: "+e.getMessage());
		}finally {
			try {
				if(bfr!=null) {
					bfr.close();
				}
			} catch (IOException e) {
				logger.error("Option Copy, BufferedReader Close Error IOException: "+e.getMessage());
			}finally {
				fw = null;
				bfr = null;
			}
		}
	}

}
