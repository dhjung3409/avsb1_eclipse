package com.terais.avsb.module;


import com.terais.avsb.core.PropertiesData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
  * 입력되어 있는 IP 정렬 클래스
  */
public class IpListSortByIp implements Comparator<String> {

    /**
      * 입력된 두개의 IP를 숫자의 크기로 비교하는 메소드
      * @param o1 IP 1번
      * @param o2 IP 2번
      * @return 두개의 IP 비교
      */
    public int compare(String o1, String o2) {
        if(o1.contains("$")) {
            o1 = o1.substring(o1.indexOf("$") + 1);
        }
        String[] ips1 = o1.split("\\.");
        String updatedIp1 = String.format("%3s.%3s.%3s.%3s",
                ips1[0],ips1[1],ips1[2],ips1[3]);
        if(o2.contains("$")) {
            o2 = o2.substring(o2.indexOf("$") + 1);
        }
        String[] ips2 = o2.split("\\.");
        String updatedIp2 = String.format("%3s.%3s.%3s.%3s",
                ips2[0],ips2[1],ips2[2],ips2[3]);
        return updatedIp1.compareTo(updatedIp2);
    }

    /**
      * IP 리스트를 정렬해서 가져오는 메소드
      * @return 정렬된 IP 리스트
      */
    public static List<String> getIP(){
        List<String> ipList = new ArrayList<String>();
        for(String ip : PropertiesData.subIp){
            ipList.add(ip);
        }
        Collections.sort(ipList, new IpListSortByIp());
        return ipList;
    }
}