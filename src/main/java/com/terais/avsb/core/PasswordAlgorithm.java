package com.terais.avsb.core;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Base64;

import com.terais.avsb.cron.CurrentCountScheduler;

public class PasswordAlgorithm {
	
	private static final Logger logger = LoggerFactory.getLogger(PasswordAlgorithm.class);

	public static String LI_Y1="2";
	
	private static final String KEY = "avsignaturebridge1deskey";

	public static String encrypt(String ID) throws Exception {
		if (ID == null || ID.length() == 0){
			return "";		
			 
		}
		String instance = (KEY.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding"; 

		Cipher	cipher = Cipher.getInstance(instance); 

		cipher.init(Cipher.ENCRYPT_MODE, getKey()); 
		String amalgam = ID; 

		byte[] inputBytes1 = amalgam.getBytes(); 

		byte[] outputBytes1 = cipher.doFinal(inputBytes1); 

		String outputStr1 = new String(Base64.encode(outputBytes1)); 
		return outputStr1; 
	} 

	public static String decrypt(String codedID) throws Exception {
		logger.debug("codedID: "+codedID);
		if (codedID == null || codedID.length() == 0){
			return ""; 
		}
		String instance = (KEY.length() == 24) ? "DESede/ECB/PKCS5Padding" : "DES/ECB/PKCS5Padding"; 
		Cipher cipher = Cipher.getInstance(instance); 

		cipher.init(Cipher.DECRYPT_MODE, getKey()); 

		byte[] code = codedID.getBytes();
		byte[] inputBytes1 = Base64.decode(code); 

		byte[] outputBytes2 = cipher.doFinal(inputBytes1); 

		String strResult = new String(outputBytes2); 
		return strResult; 
	}

	public static Key getKey() throws Exception {
		logger.debug("Key's length : "+KEY.length());
		return (KEY.length() == 24) ? getKey2(KEY) : getKey1(KEY); 
	} 

	public static Key getKey1(String keyValue) throws Exception { 
		logger.debug("DES"); 
		DESKeySpec desKeySpec = new DESKeySpec(keyValue.getBytes()); 
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES"); 
		Key key = keyFactory.generateSecret(desKeySpec); 
		return key; 
	} 

	public static Key getKey2(String keyValue) throws Exception { 
		logger.debug("Triple DES"); 
		DESedeKeySpec desKeySpec = new DESedeKeySpec(keyValue.getBytes()); 
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede"); 
		Key key = keyFactory.generateSecret(desKeySpec); 
		return key; 
	}
}
