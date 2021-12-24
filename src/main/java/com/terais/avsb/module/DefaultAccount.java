package com.terais.avsb.module;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.terais.avsb.core.PathAndConvertGson;
import org.apache.taglibs.standard.extra.spath.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.terais.avsb.core.BCryptPasswordCore;
import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.vo.LoginVO;

/**
  * 기본 계정 생성 클래스
  */
public class DefaultAccount {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAccount.class);

	/**
	  * 기본 계정정보 JSON 파일 생성 메소드
	  */
	public static void createDefaultAccount(){
		Gson gson = null;
		File file = null;
		File seqFile = null;
		try{
			gson = PathAndConvertGson.gson;
			file = new File(FilePath.accountFile);
			seqFile = new File(FilePath.accountCountFile);
			logger.debug("createDefaultAccount start");
			Properties prop = new Properties();
			
			if(!seqFile.exists()){
				seqFile.createNewFile();				
				prop.setProperty("no", "1");
				FileOutputStream fos = new FileOutputStream(seqFile);				
				prop.store(fos, seqFile.getPath());
			}
			if(!file.exists()){
				file.createNewFile();
				defaultWrite();
			}
			List<LoginVO> list = PathAndConvertGson.convertGson(FilePath.accountFile);
			if(list == null || list.size()==0){
				defaultWrite();
			}
		}catch(FileNotFoundException e){
			logger.error("FileNotFoundException: "+e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("IOException: "+e.getMessage());
		}catch(NullPointerException e){
			logger.error("NullPointerException: "+e.getMessage());
			defaultWrite();
		}catch(JsonParseException e){
			logger.error("JsonParseException: "+e.getMessage());
			defaultWrite();
		}finally{			
			if(seqFile.exists()&&PropertiesData.accountCount==-1){
				PropertiesData.callAccountSeq();
			}

			if(file!=null){
				file=null;
			}
			if(seqFile!=null){
				seqFile=null;
			}

		}
	}
	
	/**
	  * 기본 계정정보 생성 메소드
	  * @return 기본 계정정보 객체
	  */
	public static LoginVO defaultWrite(){
		Gson gson = null;
		File file = null;
		File seqFile = null;
		Properties prop = new Properties();
		LoginVO defaultAccount = null;
		try {
			gson = PathAndConvertGson.gson;
			file = new File(FilePath.accountFile);
			seqFile = new File(FilePath.accountCountFile);
			FileInputStream fis = new FileInputStream(seqFile);
			prop.load(fis);
			logger.debug("{}",prop.get("no")==null);


			if(prop.get("no")==null){
				prop.setProperty("no", "1");
				FileOutputStream fos = new FileOutputStream(seqFile);				
				prop.store(fos, seqFile.getPath());
			}
			long no = 0;
			defaultAccount = new LoginVO();
			defaultAccount.setNo(no);
			defaultAccount.setUserId(PasswordAlgorithm.encrypt("admin"));
			defaultAccount.setUserPw(BCryptPasswordCore.passwordEncoder.encode("P@ssw0rd"));
			defaultAccount.setUserRole(PasswordAlgorithm.encrypt("admin"));
			defaultAccount.setUserName(PasswordAlgorithm.encrypt("Administrator"));
			FileWriter defFw= new FileWriter(file);
			gson.toJson(defaultAccount,defFw);
			defFw.flush();
			defFw.close();
			FileOutputStream fos = new FileOutputStream(seqFile);				
			prop.setProperty("no", ++no+"");
			prop.store(fos, seqFile.getPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Create Default Account IOException: "+e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Create Default Account Exception"+e.getMessage());
		}finally{
			if(file!=null){
				file=null;
			}
			if(seqFile!=null){
				seqFile=null;
			}
		}
		return defaultAccount;
		
	}

	/**
	  * 새로 생성할 계정 고유 번호를 가져오는 메소드
	  * @return 새로 생성될 계정 고유 번호
	  */
	public static long getNo(){
		Properties prop = new Properties();
		long no = 0;
		try {
			no = Integer.parseInt(prop.get("no").toString());
		}catch(NumberFormatException e){
			logger.error("Account Sequence NumberFormatException: "+e.getMessage());
			no = checkAccountNo();
			prop.setProperty("no",String.valueOf(no));
			PropertiesData.setProp(prop, FilePath.accountCountFile);
		}catch (NullPointerException e){
			logger.error("Account Sequence NullPointerException: "+e.getMessage());
			no = checkAccountNo();
			prop.setProperty("no",String.valueOf(no));
			PropertiesData.setProp(prop, FilePath.accountCountFile);
		}catch(Exception e){
			logger.error("Account Sequence Error: "+e.getMessage());
			no = checkAccountNo();
			prop.setProperty("no",String.valueOf(no));
			PropertiesData.setProp(prop, FilePath.accountCountFile);
		}

		return no;
	}

	/**
	  * 계정 생성시 고유 번호를 갱신하는 메소드
	  * @return no - long - 갱신된 계정 고유 번호
	  */
	public static long checkAccountNo(){
		List<LoginVO> list = PathAndConvertGson.convertGson(FilePath.accountFile);
		long no = 0;
		if(list == null || list.size()==0){
			return no;
		}
		for(LoginVO vo : list){
			if(vo.getNo()>no){
				no=vo.getNo();
			}
		}

		return ++no;
	}
}	