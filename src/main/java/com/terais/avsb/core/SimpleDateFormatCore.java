package com.terais.avsb.core;

import java.text.SimpleDateFormat;

/**
  * 날짜 출력 형식 객체를 가지고 있는 클래스
  */
public class SimpleDateFormatCore {
    
	/**
	 * "yyyy/MM/dd HH:mm" 날짜 포멧
	 */
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    
	/**
	 * "yyyy/MM/dd" 날짜 포멧
	 */
	public static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
    
	/**
	 * "yyyy/MM/dd HH:mm:ss" 날짜 포멧
	 */
	public static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
}
