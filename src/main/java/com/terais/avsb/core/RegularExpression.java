package com.terais.avsb.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
  * 정규식을 가지고 있는 클래스
  */
public class RegularExpression {

	/**
	  * 입력된 IP 값을 정규식으로 검사 해, 올바른 값인지를 확인하는 메소드
	  * @param ip 입력된 IP
	  * @return 입력된 IP를 정규식에 검사한 결과 값
	  */
	public static boolean checkIP(String ip){
		Pattern pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
}
