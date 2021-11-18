package com.terais.avsb.module;


import com.terais.avsb.core.PropertiesData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IpListSortByIp implements Comparator<String> {

    public int compare(String o1, String o2) {
        o1=o1.substring(o1.indexOf("$")+1);
        String[] ips1 = o1.split("\\.");
        String updatedIp1 = String.format("%3s.%3s.%3s.%3s",
                ips1[0],ips1[1],ips1[2],ips1[3]);
        o2=o2.substring(o1.indexOf("$")+1);
        String[] ips2 = o2.split("\\.");
        String updatedIp2 = String.format("%3s.%3s.%3s.%3s",
                ips2[0],ips2[1],ips2[2],ips2[3]);
        return updatedIp1.compareTo(updatedIp2);
    }

    public static List<String> getIP(){
        List<String> ipList = new ArrayList<String>();
        for(String ip : PropertiesData.subIp){
            ipList.add(ip);
        }
        Collections.sort(ipList, new IpListSortByIp());
        return ipList;
    }
}