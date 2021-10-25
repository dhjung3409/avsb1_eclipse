package com.terais.avsb.module;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;

public class IpListSortByIp implements Comparator<String> {

    public int compare(String o1, String o2) {
        String[] ips1 = o1.split("\\.");
        String updatedIp1 = String.format("%3s.%3s.%3s.%3s",
                ips1[0],ips1[1],ips1[2],ips1[3]);
        String[] ips2 = o2.split("\\.");
        String updatedIp2 = String.format("%3s.%3s.%3s.%3s",
                ips2[0],ips2[1],ips2[2],ips2[3]);
        return updatedIp1.compareTo(updatedIp2);
    }
}