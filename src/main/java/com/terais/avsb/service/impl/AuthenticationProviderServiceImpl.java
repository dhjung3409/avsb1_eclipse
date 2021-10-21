package com.terais.avsb.service.impl;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.terais.avsb.core.BCryptPasswordCore;
import com.terais.avsb.core.PasswordAlgorithm;
import com.terais.avsb.vo.LoginVO;


public class AuthenticationProviderServiceImpl implements AuthenticationProvider{

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationProviderServiceImpl.class);
	
	@Autowired
	private LoginServiceImpl loginService;
	
	@Autowired
	private AccountServiceImpl accountService;
	
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String userId = authentication.getPrincipal().toString();
		String userPw = authentication.getCredentials().toString();
		LoginVO loginInfo = loginService.loadUserByUsername(userId);
		if(loginInfo==null){
			logger.debug("userIdCheck==0");
			throw new BadCredentialsException(userId);
		}
		logger.debug("Authorities: "+loginInfo.getAuthorities());
		logger.debug(userPw);
		logger.debug(loginInfo.getPassword());
		
		boolean result = BCryptPasswordCore.passwordEncoder.matches(userPw.trim(), loginInfo.getUserPw().trim());
		logger.debug(result+"");
		if(!result){
			logger.debug("Password Not Correct");
			throw new BadCredentialsException(userPw);
		}
		logger.debug("Password Correct");
		loginInfo.setUserPw(userPw);
		accountService.editAccount(loginInfo, true, "PUT");
		try {
			String accountId = PasswordAlgorithm.decrypt(loginInfo.getUserId());
			String accountRole;
			if(PropertiesData.licenseStatus==false) {
				accountRole = "expired";
			}else{
				accountRole = PasswordAlgorithm.decrypt(loginInfo.getUserRole());
			}
			String accountName = PasswordAlgorithm.decrypt(loginInfo.getUserName());		
			loginInfo.setUserName(accountName);
			loginInfo.setUserId(accountId);
			loginInfo.setUserRole(accountRole);
		} catch (Exception e) {
			logger.error("Authentication Exception: "+e.getMessage());
		}
		logger.debug("Set Authorities: "+loginInfo.getAuthorities());
		logger.info("Login user: "+userId);
		UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userId, userPw, loginInfo.getAuthorities());
		
		
		return user;
	}
	
	public boolean supports(Class<?> arg0) {
		return true;
	}

}
