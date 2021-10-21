package com.terais.avsb.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegularExpression {

	public static boolean checkIP(String ip){
		Pattern pattern = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches();
	}
}
