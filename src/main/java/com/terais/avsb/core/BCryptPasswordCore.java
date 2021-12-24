package com.terais.avsb.core;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
  * BCryptPasswordEncoder static 객체 생성 클래스
  */
public class BCryptPasswordCore {

	/**
	 * 암호화 복호화에 사용하는 BCryptPasswordEncoder static 객체
	 */
	public static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
}
