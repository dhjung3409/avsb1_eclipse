package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
  * 서버별 실시간 검사 수를 가지고있는 메소드
  */
public class Current {
    /**
     * IP
     */
    @JsonProperty
    private String ip;
    /**
     * 실시간 검사 수
     */
    @JsonProperty
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
