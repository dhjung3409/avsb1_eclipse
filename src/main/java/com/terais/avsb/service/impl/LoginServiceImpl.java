package com.terais.avsb.service.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.cron.SubIPCheckSchduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.core.PathAndConvertGson;
import com.terais.avsb.module.FilePath;
import com.terais.avsb.service.LoginService;
import com.terais.avsb.vo.LoginVO;


public class LoginServiceImpl implements LoginService,UserDetailsService{
	

	private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

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
			
		} catch (FileNotFoundException e) {
			logger.error("getLogin FileNotFoundException Failed: "+e.getMessage());
		} catch (Exception e) {
			logger.error("getLogin Exception Failed: "+e.getMessage());
		}
		return null;
	}

	public LoginVO loadUserByUsername(String userId) throws UsernameNotFoundException {
		logger.debug(userId);
		LoginVO loginInfo = getLogin(userId);		
		if(loginInfo==null){
			throw new UsernameNotFoundException(userId);
		}
		
		logger.debug(loginInfo.toString());
		return loginInfo;
	}

	public Map<String,Object> getLoginUser(Authentication auth){
		Map<String,Object> user = new HashMap<String, Object>();
		user.put("userInfo",auth.getPrincipal());
		user.put("userRole",auth.getAuthorities());
		user.put("licenseStatus", PropertiesData.licenseRemain);

		return user;
	}
	
}