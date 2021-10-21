package com.terais.avsb.vo;


import com.terais.avsb.core.SimpleDateFormatCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;

public class CurrentLogVO implements  Comparable<CurrentLogVO>,Comparator<CurrentLogVO> {

    private static final Logger logger = LoggerFactory.getLogger(CurrentLogVO.class);

    private String ip;
    private String time;
    private String path;
    private String result;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }



    @Override
    public String toString() {
        return "CurrentLog{" +
                "ip='" + ip + '\'' +
                ", time=" + time +
                ", path='" + path + '\'' +
                ", result='" + result + '\'' +
                '}';
    }

    public int compareTo(CurrentLogVO o) {
        return 0;
    }

    public int compare(CurrentLogVO o1, CurrentLogVO o2) {
        Date date1 = null;
        Date date2 = null;
        try {
            date1 = SimpleDateFormatCore.sdf3.parse(o1.time);
            date2 = SimpleDateFormatCore.sdf3.parse(o2.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date2.compareTo(date1);
    }
}
