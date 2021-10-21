package com.terais.avsb.dto;

import java.util.List;

public class Current {

    private String ip;
    private List<Integer> count;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<Integer> getCount() {
        return count;
    }

    public void setCount(List<Integer> count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Current{" +
                "ip='" + ip + '\'' +
                ", count=" + count +
                '}';
    }
}
