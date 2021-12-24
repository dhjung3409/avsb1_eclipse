package com.terais.avsb.core;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

import com.terais.avsb.cron.CurrentCountScheduler;

/**
  * Triple DES or DES 암호화 클래스
  */
public class PasswordAlgorithm {

	private static final Logger logger = LoggerFactory.getLogger(PasswordAlgorithm.class);

	/**
	 * 라이센스 기간 중 연도의 천의자리 수
	 */
	public static String LI_Y1="2";
	
	/**
	 * Triple DES 암호화 키에 사용 할 24자리 문자열
	 */
	private static final String KEY = "avsignaturebridge1deskey";

	/**
	  * 입력된 문자열을 암호화하는 메소드
	  * @param ID 암호화 시킬 문자열
	  * @return 암호화된 문자열
	  */
	public static String encrypt(String ID){
		if (ID == null || ID.length() == 0){
			return "";		
			 
		}
		String instance = (KEY.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";

		Cipher	cipher = null;
		try {
			cipher = Cipher.getInstance(instance);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Encrypt Cipher NoSuchAlgorithmException: "+e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("Encrypt Cipher NoSuchPaddingException: "+e.getMessage());
		}

		try {
			cipher.init(Cipher.ENCRYPT_MODE, getKey());
		} catch (Exception e) {
			logger.error("Encrypt Cipher init Exception: "+e.getMessage());
		}
		String amalgam = ID; 

		byte[] inputBytes1 = amalgam.getBytes();

		byte[] outputBytes1 = new byte[0];

		try {
			outputBytes1 = cipher.doFinal(inputBytes1);
		} catch (IllegalBlockSizeException e) {
			logger.error("Encrypt outputBytes1 IllegalBlockSizeException: "+e.getMessage());
		} catch (BadPaddingException e) {
			logger.error("Encrypt outputBytes1 BadPaddingException: "+e.getMessage());
		}

		String outputStr1 = new String(Base64.encode(outputBytes1)); 
		return outputStr1; 
	} 

	/**
	  * 입력된 암호화된 문자열을 복호화 시키는 메소드
	  * @param codedID 복호화 시킬 암호화된 문자열
	  * @return 복호화된 문자열
	  */
	public static String decrypt(String codedID){
		logger.debug("codedID: "+codedID);
		if (codedID == null || codedID.length() == 0){
			return ""; 
		}
		String instance = (KEY.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding";
		Cipher cipher = null;
		try {
			cipher = Cipher.getInstance(instance);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Decrypt Cipher NoSuchAlgorithmException: "+e.getMessage());
		} catch (NoSuchPaddingException e) {
			logger.error("Decrypt Cipher NoSuchPaddingException: "+e.getMessage());
		}

		try {
			cipher.init(Cipher.DECRYPT_MODE, getKey());
		} catch (Exception e) {
			logger.error("Decrypt Cipher init Exception: "+e.getMessage());
		}

		byte[] code = codedID.getBytes();
		byte[] inputBytes1 = Base64.decode(code);

		byte[] outputBytes2 = new byte[0];
		try {
			outputBytes2 = cipher.doFinal(inputBytes1);
		} catch (IllegalBlockSizeException e) {
			logger.error("Decrypt outputBytes2 IllegalBlockSizeException: "+e.getMessage());
		} catch (BadPaddingException e) {
			logger.error("Decrypt outputBytes2 BadPaddingException: "+e.getMessage());
		}

		String strResult = new String(outputBytes2);
		return strResult; 
	}

	/**
	  * KEY의 길이에 따라 암호화의 종류를 바꾸는 메소드
	  * @return 선택된 암호화 종류
	  */
	public static Key getKey() {
		logger.debug("Key's length : "+KEY.length());
		Key key = null;
		try {
			key = (KEY.length() == 24) ? getKey2(KEY) : getKey1(KEY);
		} catch (Exception e) {
			logger.error("getKey return Exception: "+e.getMessage());
		}finally{
			return key;
		}
	} 

	/**
	  * DES 암호화 키를 가져오는 메소드
	  * @param keyValue 사용될 KEY 값
	  * @return DES 암호화 키
	  */
	public static Key getKey1(String keyValue){
		logger.debug("DES");
		DESKeySpec desKeySpec = null;
		try {
			desKeySpec = new DESKeySpec(keyValue.getBytes());
		} catch (InvalidKeyException e) {
			logger.error("getKey1 desKeySpec InvalidKeyException: "+e.getMessage());
		}
		SecretKeyFactory keyFactory = null;
		try {
			keyFactory = SecretKeyFactory.getInstance("DES");
		} catch (NoSuchAlgorithmException e) {
			logger.error("getKey1 keyFactory NoSuchAlgorithmException: "+e.getMessage());
		}
		Key key = null;
		try {
			key = keyFactory.generateSecret(desKeySpec);
		} catch (InvalidKeySpecException e) {
			logger.error("getKey1 key InvalidKeySpecException: "+e.getMessage());
		}
		return key; 
	} 

	/**
	  * Triple DES 암호화 키를 가져오는 메소
	  * @param keyValue 사용될 KEY 값
	  * @return Triple DES 암호화 키
	  */
	public static Key getKey2(String keyValue){
		logger.debug("Triple DES");
		DESedeKeySpec desKeySpec = null;

		try {
			desKeySpec = new DESedeKeySpec(keyValue.getBytes());
		} catch (InvalidKeyException e) {
			logger.error("getKey2 desKeySpec InvalidKeyException: "+e.getMessage());
		}

		SecretKeyFactory keyFactory = null;

		try {
			keyFactory = SecretKeyFactory.getInstance("DESede");
		} catch (NoSuchAlgorithmException e) {
			logger.error("getKey2 keyFactory NoSuchAlgorithmException: "+e.getMessage());
		}

		Key key = null;

		try {
			key = keyFactory.generateSecret(desKeySpec);
		} catch (InvalidKeySpecException e) {
			logger.error("getKey2 key InvalidKeySpecException: "+e.getMessage());
		}

		return key; 
	}
}
