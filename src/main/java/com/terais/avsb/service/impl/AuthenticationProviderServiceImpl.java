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

import java.util.HashMap;
import java.util.Map;


/**
  * 로그인의 성공 실패 여부를 판단하는 클래스
  */
public class AuthenticationProviderServiceImpl implements AuthenticationProvider{

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationProviderServiceImpl.class);
	
	/**
	 * bean 등록된 LoginServiceImpl 클래스 객체
	 */
	@Autowired
	private LoginServiceImpl loginService;
	
	/**
	 * bean 등록 된 AccountServiceImpl 클래스 객체
	 */
	@Autowired
	private AccountServiceImpl accountService;
	
	/**
	  * /login 페이지에 입력된 정보와 보유하고 있는 계정 정보를 대조해서 일치 했을 때 로그인 시켜주는 메소드
	  * @param authentication /login 페이지에서 받아온 로그인 정보
	  * @return 로그인 성공한 계정 정보
	  */
	public Authentication authenticate(Authentication authentication){
		
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
		//try {
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
		/*} catch (Exception e) {
			logger.error("Authentication Exception: "+e.getMessage());
		}*/
		logger.debug("Set Authorities: "+loginInfo.getAuthorities());
		logger.info("Login user: "+userId);
		Map<String,String> userInfo = new HashMap<String, String>();
		userInfo.put("userId",userId);
		userInfo.put("userName",loginInfo.getUserName());
		UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userInfo, userPw, loginInfo.getAuthorities());
		
		
		return user;
	}
	

	public boolean supports(Class<?> arg0) {
		return true;
	}

}
