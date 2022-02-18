package com.terais.avsb.module;

import com.terais.avsb.core.PropertiesData;

/**
  * 서버의 OS 종류를 확인하는 클래스
  */
public class CheckOS {

    public static String osSeparator = "/";
    
	/**
      * AVSB가 실행된 서버의 os 종류를 확인하는 메소드
      */
    public static void osCheck(){
        String os = System.getProperty("os.name").toLowerCase();
        String bit = System.getProperty("os.arch");
        if(os.contains("win")){
            PropertiesData.osName="win";
            osSeparator="\\";
        }else if(os.contains("linux")&&bit.contains("64")){
            PropertiesData.osName="linux64";
        }else if(os.contains("hp")&&bit.contains("64")){
            PropertiesData.osName="hp64";
        }else if(os.contains("aix")&&bit.contains("64")){
            PropertiesData.osName="aix64";
        }else if((os.contains("sun")||os.contains("sol"))&&bit.contains("64")){
            PropertiesData.osName="sol64";
        }else if(os.contains("linux")){
            PropertiesData.osName="linux";
        }else if(os.contains("hp")){
            PropertiesData.osName="hp";
        }else if(os.contains("aix")){
            PropertiesData.osName="aix";
        }else if(os.contains("sun")||os.contains("sol")){
            PropertiesData.osName="solx86";
        }else{

        }
    }
}
