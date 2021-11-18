package com.terais.avsb.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.terais.avsb.core.BCryptPasswordCore;
import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.DefaultAccount;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.AccountService;
import com.terais.avsb.vo.LoginVO;

public class AccountServiceImpl implements AccountService{

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	public LoginVO createAccount(String userId,String userPw, String userName, String userRole) {
		LoginVO user = new LoginVO();
		String fileName = FilePath.accountFile;
		String seqFileName = FilePath.accountCountFile;
		File file = new File(fileName);
		File seqFile = new File(seqFileName);
		Properties pro = new Properties();
		try {
			logger.debug("method try Start");
			DefaultAccount.createDefaultAccount();
			long no = PropertiesData.accountCount;
			List<LoginVO> account = PathAndConvertGson.convertGson(fileName);
			logger.debug(user.getUserPw());
			logger.debug(account.get(0).toString());
			if (checkIDOverlap(account, userId)) {
				logger.debug("Overlap ID");
				return user;
			} else {
				logger.debug("User Info save");
				user.setNo(no);
				user.setUserId(PasswordAlgorithm.encrypt(userId));
				user.setUserPw(BCryptPasswordCore.passwordEncoder.encode(userPw));
				user.setUserName(PasswordAlgorithm.encrypt(userName));
				user.setUserRole(PasswordAlgorithm.encrypt(userRole));
				account.add(user);
				FileWriter fw = new FileWriter(file);
				FileOutputStream fos = new FileOutputStream(seqFile);
				pro.setProperty("no", ++no + "");
				PropertiesData.accountCount = no;
				pro.store(fos, seqFileName);
				PathAndConvertGson.gson.toJson(account, fw);
				fw.flush();
				fw.close();
			}
		} catch (FileNotFoundException e) {
			logger.error("Create Account FileNotFoundException: " + e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Create Account IOException: " + e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Create Account Exception: " + e.getMessage());
		}
		return user;
	}

	public boolean checkIDOverlap(List<LoginVO> list, String userId){
		boolean result = false;
		String registerId = "";
		for(LoginVO id : list){
			try {
				registerId = PasswordAlgorithm.decrypt(id.getUserId());
				if (registerId.equals(userId)){
					logger.debug(registerId+" : "+userId);
					result=true;
				}
			} catch (Exception e) {
				logger.error("Account id check decrypt Error: "+e.getMessage());
				result = true;
			}
		}
		return result;
	}


	public LoginVO editAccount(LoginVO user, boolean status, String method) {
		logger.debug("editAccount Start");
		String fileName = FilePath.accountFile;		
		List<LoginVO> accountList=PathAndConvertGson.convertGson(fileName);
		LoginVO loginVO=null;

		try{
			logger.debug("try catch Start");
			for(int i=0; i<accountList.size();i++){
				logger.debug("for Start");
				loginVO = accountList.get(i);
				logger.debug("loginVO.NO: "+loginVO.getNo());
				logger.debug("user.NO: "+user.getNo());
				if(loginVO.getNo()==user.getNo()&&method.equals("POST")){
					logger.debug(loginVO.toString());
					logger.debug(user.toString());
					accountList.remove(i--);
					if(user.getUserPw()!=null&&status==true){
						loginVO.setUserPw(BCryptPasswordCore.passwordEncoder.encode(user.getUserPw()));	
					}
					
					if(user.getUserRole()!=null){
						loginVO.setUserRole(PasswordAlgorithm.encrypt(user.getUserRole()));
					}			
					if(user.getUserName()!=null){
						loginVO.setUserName(PasswordAlgorithm.encrypt(user.getUserName()));
					}
					accountList.add(loginVO);					
					break;
				}else if(loginVO.getNo()==user.getNo()&&method.equals("DELETE")){
					accountList.remove(i);
					loginVO=null;
					break;
				}else{
					loginVO=null;
					continue;
				}			
			}
			FileWriter fw = new FileWriter(new File(fileName));
			PathAndConvertGson.gson.toJson(accountList,fw);
			fw.flush();
			fw.close();
		}catch(IOException e){
			logger.error("Edit Account IOException: "+e.getMessage());
		} catch (Exception e) {
			logger.error("Edit Account Error: "+e.getMessage());
		} finally{
			accountList=null;
		}
		return loginVO;
	}

	public void delAccount(List<String> no){
		logger.debug("delAccount start");
		try{
			if(no.isEmpty()){
				logger.debug("account no is empty");
				return;
			}
			for(String accountNo: no){
				Integer.parseInt(accountNo);
				if(PropertiesData.accountCount<Integer.parseInt(accountNo)){
					logger.debug("input account no data > seqNo");
					return;
				}else if(Integer.parseInt(accountNo)<1){
					logger.debug("input account no data < 1");
					return;
				}
			}
		}catch(NumberFormatException e){
			logger.error("delAccount NumberFormatException: "+e.getMessage());
			return;
		}catch(NullPointerException e){
			logger.error("delAccount NullPointerException: "+e.getMessage());
			return;
		}
		String fileName = FilePath.accountFile;		
		List<LoginVO> accountList=PathAndConvertGson.convertGson(fileName);
		FileWriter fw=null;	
		
		try {
			logger.debug(no.toString());
			for(int i=0;i<accountList.size();i++){				
				if(Collections.frequency(no, String.valueOf(accountList.get(i).getNo()))>0){
					logger.debug("account remove");
					accountList.remove(accountList.get(i));
					i--;
					continue;
				}
			}
			fw = new FileWriter(new File(fileName));
			logger.debug("login.json FileWrite");
			PathAndConvertGson.gson.toJson(accountList,fw);
			fw.flush();
			
		} catch (IOException e) {
			logger.error("delAccount IOException: "+e.getMessage());
		}finally{			
			try {
				fw.close();
			} catch (IOException e) {
				logger.error("FileWrite.close() IOException: "+e.getMessage());
			}
		}
		
	}

	public List<Object> viewAccountList(String myAccount){

		String[] splitAccount = myAccount.split(" ");
		String name=null;
		for(String text: splitAccount){
			if(text.contains("userId")){
				text = text.replaceAll("}"," ");
				text.trim();
				name = text.substring(text.indexOf("userId=")+7);
				name = name.replace(",","");
			}
		}

		name = name.trim();

		String fileName = FilePath.accountFile;		
		List<LoginVO> accountList=PathAndConvertGson.convertGson(fileName);
		List<Object> accounts = new ArrayList<Object>();
		if(name!=null) {
			try {
				logger.debug("LoginId: ["+name+"]");
				for (LoginVO account : accountList) {
					logger.debug("userId: "+PasswordAlgorithm.decrypt(account.getUserId()));

					if ((PasswordAlgorithm.decrypt(account.getUserId())).equals(name)) {
						logger.debug("This is LoginId");
						continue;
					}
					accounts.add(addAccount(account));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("View Account Exception: " + e.getMessage());
			}
			logger.debug(accounts.toString());
		}else{

		}
		return accounts;
	}

	public List<Object> addAccount(LoginVO account){
		List<Object> accountSource=new ArrayList<Object>();
		accountSource.add(account.getNo());
		String userId=null;
		String userRole=null;
		String userName=null;
		try {
			userId = PasswordAlgorithm.decrypt(account.getUserId());			
			userRole = PasswordAlgorithm.decrypt(account.getUserRole());
			userName = PasswordAlgorithm.decrypt(account.getUserName());
			accountSource.add(userId);		
			accountSource.add(userRole);		
			accountSource.add(userName);
		} catch (Exception e) {
			logger.error("Account Algorithm Exception: "+e.getMessage());
		}
		return accountSource;
	}

	public boolean changePassword(String id, String nowPass, String newPass){
		boolean result = false;
		logger.debug("nowPass: "+nowPass);
		logger.debug("newPass: "+newPass);
		List<LoginVO> accountList=PathAndConvertGson.convertGson(FilePath.accountFile);
		LoginVO user = null;
		int listPoint = -1;
		for(LoginVO account: accountList){
			try {
				if(PasswordAlgorithm.decrypt(account.getUserId()).equals(id)){
					listPoint = accountList.indexOf(account);
					user = account;
					break;
				}
			} catch (Exception e) {
				logger.error("Not Found Account: "+e.getMessage());
				result = false;
			}
		}

		if(user==null){
			return false;
		}

		if(BCryptPasswordCore.passwordEncoder.matches(nowPass,user.getUserPw())){
			user.setUserPw(BCryptPasswordCore.passwordEncoder.encode(newPass));
			accountList.set(listPoint,user);
			FileWriter fw = null;
			try {
				fw = new FileWriter(FilePath.accountFile);
				PathAndConvertGson.gson.toJson(accountList, fw);
				fw.flush();
				result = true;
			} catch (IOException e) {
				logger.error("Account Info File Save Error: "+e.getMessage());
				result = false;
			}


		}else{
			logger.debug("Account Password Not Match");
		}

		return result;
	}
}