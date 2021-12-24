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

/**
  * 라이센스 상태 확인하는 클래스
  */
public class LicenseCheck extends License {

    private static final Logger logger = LoggerFactory.getLogger(LicenseCheck.class);

    /**
     * 라이센스 상태 값
     */
    private boolean result;

    /**
     * License 생성자를 이용해서 라이센스의 유효성을 확인하는 생성자
     */
    public LicenseCheck(){
        super();
        result = this.checkLicense(getLicense());
        logger.debug("license result: "+result);
        PropertiesData.licenseStatus=result;
    }

    /**
      * 입력된 라이센스 키를 가져오는 메소드
      * @return licenseKey - String - 입력된 라이센스 키
      */
    public String getLicense(){
        Properties prop = PropertiesData.getProp(FilePath.license);
        String licenseKey = prop.getProperty("serial");
        logger.debug("serialKey: "+licenseKey);
        return licenseKey;
    }

    /**
      *  라이센스 기간을 확인하는 메소드
      */
    public static void checkPeriod(){
        try {
            Date expire= SimpleDateFormatCore.sdf2.parse(PropertiesData.licenseExpire);
            logger.debug("license expired: "+SimpleDateFormatCore.sdf2.format(expire));
            Date today = new Date();
            Calendar cl = Calendar.getInstance();
            cl.setTime(expire);
            cl.add(Calendar.MONTH,-1);
            Date monthAgo = cl.getTime();
            logger.debug(SimpleDateFormatCore.sdf2.format(monthAgo));
            int monthResult = monthAgo.compareTo(today);
            int result = expire.compareTo(today);
            logger.debug("date monthResult: "+monthResult);
            logger.debug("date result: "+result);
            if(monthResult<0){
                PropertiesData.licenseMonthStatus=false;
                long diff = expire.getTime()-today.getTime();
                long calDiff = diff/(24*60*60*1000);
                logger.debug("time: "+diff);
                int dayDiff = (int) calDiff+1;
                logger.debug("라이센스 만료 "+dayDiff+"일 전");
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
