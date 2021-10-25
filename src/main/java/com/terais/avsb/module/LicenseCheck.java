package com.terais.avsb.module;

import com.terais.avsb.core.License;
import com.terais.avsb.core.PropertiesData;
import com.terais.avsb.core.SimpleDateFormatCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class LicenseCheck extends License {

    private static final Logger logger = LoggerFactory.getLogger(LicenseCheck.class);

    private boolean result;

    public LicenseCheck(){
        super();
        result = this.checkLicense(getLicense());
        logger.debug("license result: "+result);
        PropertiesData.licenseStatus=result;
    }

    public String getLicense(){
        Properties prop = PropertiesData.getProp(FilePath.license);
        String licenseKey = prop.getProperty("serial");
        logger.debug("serialKey: "+licenseKey);
        return licenseKey;
    }

    public static void checkPeriod(){
        try {
            Date expire= SimpleDateFormatCore.sdf2.parse(PropertiesData.licenseExpire);
            logger.info("license expired: "+SimpleDateFormatCore.sdf2.format(expire));
            Date today = new Date();
            Calendar cl = Calendar.getInstance();
            cl.setTime(expire);
            cl.add(Calendar.MONTH,-1);
            Date monthAgo = cl.getTime();
            logger.info(SimpleDateFormatCore.sdf2.format(monthAgo));
            int monthResult = monthAgo.compareTo(today);
            int result = expire.compareTo(today);
            logger.info("date monthResult: "+monthResult);
            logger.info("date result: "+result);
            if(monthResult<0){
                PropertiesData.licenseMonthStatus=false;
                long diff = expire.getTime()-today.getTime();
                long calDiff = diff/(24*60*60*1000);
                int dayDiff = (int) calDiff;
//                calDiff=Math.abs(calDiff);
                logger.info("라이센스 만료 "+dayDiff+"일 전");
                PropertiesData.licenseRemain=dayDiff;
                if(result<0){
                    PropertiesData.licenseStatus=false;
                }
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
