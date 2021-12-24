package com.terais.avsb.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.LoginService;
import com.terais.avsb.vo.LoginVO;


/**
  * 로그인 사용자에 대한 정보를 가져오는 클래스
  */
public class LoginServiceImpl implements LoginService,UserDetailsService{

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

	/**
	  * 로그인한 사용자 아이디로 로그인 유저에 대한 정보를 가져오는 메소드
	  * @param userId 로그인 사용자 아이디
	  * return 로그인 사용자 계정 정보
	  */
	public LoginVO getLogin(String userId){	
		logger.debug("getLogin Start");
		logger.debug("userId: "+userId);
		
		String fileName = FilePath.accountFile;
		try {
			logger.debug("method try Start");
			List<LoginVO> accountList=PathAndConvertGson.convertGson(fileName);
			logger.debug("JSON Read");
			for(LoginVO login:accountList){
				String accountId = PasswordAlgorithm.decrypt(login.getUserId());
				logger.debug("accountId : "+accountId);
				logger.debug("userId : "+userId);
				if(accountId.equals(userId)){
					logger.debug("bcrypt pass");
					return login;			
				}
			}
			
		}/* catch (FileNotFoundException e) {
			logger.error("getLogin FileNotFoundException Failed: "+e.getMessage());
		}*/ catch (Exception e) {
			logger.error("getLogin Exception Failed: "+e.getMessage());
		}
		return null;
	}

	/**
	  * 입력된 유저 아이디로 해당 계정의 존재 여부, 계정이 존재하는 경우 계정 정보를 가져오는 메소드
	  * @param userId 입력된 유저 아이디
	  * @return 유저 아이디로 확인된 계정 정보
	  */
	public LoginVO loadUserByUsername(String userId) throws UsernameNotFoundException {
		logger.debug(userId);
		LoginVO loginInfo = getLogin(userId);		
		if(loginInfo==null){
			throw new UsernameNotFoundException(userId);
		}
		
		logger.debug(loginInfo.toString());
		return loginInfo;
	}

	/**
	  * 유저 이름, 권한, 라이센스 유효기간을 가져오는 메소드
	  * @param auth 로그인한 유저 정보
	  * @return 유저 이름, 권한, 라이센스 유효기간
	  */
	public Map<String,Object> getLoginUser(Authentication auth){
		Map<String,Object> user = new HashMap<String, Object>();
		user.put("userInfo",auth.getPrincipal());
		user.put("userRole",auth.getAuthorities());
		user.put("licenseStatus", PropertiesData.licenseRemain);

		return user;
	}
	
}