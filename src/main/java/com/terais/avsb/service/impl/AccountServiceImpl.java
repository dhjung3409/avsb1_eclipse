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

import com.terais.avsb.core.BCryptPasswordCore;
import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.module.DefaultAccount;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.AccountService;
import com.terais.avsb.vo.LoginVO;

/**
  * 계정 정보를 다루는 서비스 클래스
  */
public class AccountServiceImpl implements AccountService{

	private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

	/**
	  * 계정 생성 메소드
	  * @param userId 아이디
	  * @param userPw 패스워드
	  * @param userName 사용자 이름
	  * @param userRole 사용자 권한
	  * @return 생성된 계정 정보
	  */
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

	/**
	  * ID 중복 처리 확인
	  * @param list 계정 정보 리스트
	  * @param userId 생성하려는 아이디
	  * @return 계정의 중복 여부
	  */
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


	/**
	  * 계정 정보를 수정하는 메소드
	  * @param user 수정하려는 계정 정보
	  * @param status 패스워드 변경 여부
	  * @param method 메소드 실행 분류 | 'POST' : 계정 정보 변경 | 'PUT' : 로그인 계정 패스워드 암호화 갱신
	  * @return 변경된 유저 정보
	  */
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
				}/*else if(loginVO.getNo()==user.getNo()&&method.equals("DELETE")){
					accountList.remove(i);
					loginVO=null;
					break;
				}*/else{
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

	/**
	  * 계정 정보 삭제
	  * @param no 삭제할 계정 고유 번호의 리스트
	  */
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

	/**
	  * 계정 정보 출력
	  * @param myAccount - 계정 정보를 요청한 계정
	  * @return 해당 계정이 조회 할 수 있는 모든 계정 정보
	  */
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

	/**
	  * viewAccountList 메소드에서 계정 정보를 요구 할 때, 암호화된 계정 정보를 복호화 시켜주는 메소드
	  * @param account 요구된 계정 정보
	  * @return 복호화된 계정 정보
	  */
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

	/**
	  * 현재 접속한 계정의 패스워드를 변경하는 메소드
	  * @param id 현재 접속한 계정
	  * @param nowPass 현재 패스워드
	  * @param newPass 변경 할 패스워드
	  * @return 계정 정보 변경 성공 여부
	  */
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