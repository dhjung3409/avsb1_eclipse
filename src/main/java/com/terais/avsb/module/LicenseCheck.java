package com.terais.avsb.module;

import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class LicenseCheck {

    private static final Logger logger = LoggerFactory.getLogger(LicenseCheck.class);

    public static void getLicense(){
        File license = new File(FilePath.license);
        Properties pro;
        if(license.exists()){
            try {
                pro=PropertiesData.getProp(license.getCanonicalPath());
                PropertiesData.licenseExpire = pro.getProperty("expired");
                logger.debug(PropertiesData.licenseExpire);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date expireDate = sdf.parse(PropertiesData.licenseExpire);
                Date today = new Date();
                boolean expireCheck = expireDate.after(today);
                PropertiesData.licenseStatus = expireCheck;
                if(!expireCheck){
                    pro.setProperty("expired",PropertiesData.licenseExpire);
                    pro.setProperty("status",String.valueOf(false));
                    PropertiesData.setProp(pro,license.getCanonicalPath());
                }
                logger.debug(expireCheck+"");
            } catch (FileNotFoundException e) {
                logger.error("FileInputStream Error: "+e.getMessage());
            } catch (IOException e) {
                logger.error("Properties load Failed: "+e.getMessage());
            } catch (ParseException e) {
                logger.error("String to Date Failed: "+e.getMessage());
            }
        }else{
            PropertiesData.licenseExpire="License expired";
            PropertiesData.licenseStatus=false;
        }
    }
    public static void checkLicenseFile() throws IOException {
        File file = new File(FilePath.license);
        File copyFile = new File(FilePath.copyLicense);
        File copyDateFile = new File(FilePath.copyDateLicense);
        Properties licensePro = PropertiesData.getProp(file.getCanonicalPath());
        Properties copyPro = new Properties();
        Properties copyDatePro = new Properties();

        if(!file.exists()&&!copyFile.exists()&&!copyDateFile.exists()){
            PropertiesData.licenseStatus=false;
            return;
        }

        if(!copyFile.exists()&&file.exists()){
            copyFile.createNewFile();
            copyPro.setProperty("expired",licensePro.getProperty("expired"));
            copyPro.setProperty("status",licensePro.getProperty("status"));
            PropertiesData.setProp(copyPro,copyFile.getCanonicalPath());
        }

        if(!copyDateFile.exists()&&file.exists()){
            copyDateFile.createNewFile();
            copyDatePro.setProperty("expired",licensePro.getProperty("expired"));
            copyDatePro.setProperty("status",licensePro.getProperty("status"));
            PropertiesData.setProp(copyDatePro,copyDateFile.getCanonicalPath());
        }

        copyPro = PropertiesData.getProp(copyFile.getCanonicalPath());
        copyDatePro = PropertiesData.getProp(copyDateFile.getCanonicalPath());

        if(!copyPro.getProperty("expired").equals(licensePro.getProperty("expired"))||!copyDatePro.getProperty("expired").equals(licensePro.getProperty("expired"))){
            logger.debug("expired error");
            setProperty(licensePro,copyPro,copyDatePro,"expired");
        }

        if(!copyPro.getProperty("status").equals(licensePro.getProperty("status"))||!copyDatePro.getProperty("status").equals(licensePro.getProperty("status"))){
            setProperty(licensePro,copyPro,copyDatePro,"status");
        }

    }
    public static void setProperty(Properties licensePro,Properties copyPro,Properties copyDatePro,String property){
        if(copyDatePro.getProperty(property).equals(licensePro.getProperty(property))){
            logger.debug("copyLicense mod");
            copyPro.setProperty(property,copyDatePro.getProperty(property));
            PropertiesData.setProp(copyPro,FilePath.copyLicense);
        }else if(copyDatePro.getProperty(property).equals(copyPro.getProperty(property))){
            logger.debug("license mod");
            licensePro.setProperty(property,copyDatePro.getProperty(property));
            PropertiesData.setProp(licensePro,FilePath.license);
        }else if(copyPro.getProperty(property).equals(licensePro.getProperty(property))){
            logger.debug("copyDateLicense mod");
            copyDatePro.setProperty(property,licensePro.getProperty(property));
            PropertiesData.setProp(copyDatePro,FilePath.copyDateLicense);
        }else{
            setAllStatus(licensePro,copyPro,copyDatePro,false);
        }
    }
    public static void setAllStatus(Properties licensePro,Properties copyPro,Properties copyDatePro,boolean status){
        licensePro.setProperty("status",String.valueOf(status));
        copyPro.setProperty("status",String.valueOf(status));
        copyDatePro.setProperty("status",String.valueOf(status));
        PropertiesData.setProp(licensePro,FilePath.license);
        PropertiesData.setProp(copyPro,FilePath.copyLicense);
        PropertiesData.setProp(copyDatePro,FilePath.copyDateLicense);
    }


}
