package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScanResultCount {
    @JsonProperty
    int total;
    @JsonProperty
    int normal;
    @JsonProperty
    int infected;
    @JsonProperty
    int cured;
    @JsonProperty
    int failed;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getInfected() {
        return infected;
    }

    public void setInfected(int infected) {
        this.infected = infected;
    }

    public int getCured() {
        return cured;
    }

    public void setCured(int cured) {
        this.cured = cured;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return "ScanResultCount{" +
                "total=" + total +
                ", normal=" + normal +
                ", infected=" + infected +
                ", cured=" + cured +
                ", failed=" + failed +
                '}';
    }

    public boolean checkZero(){
        boolean check;
        if((total+normal+infected+cured+failed)==0) {
            check = true;
        }else{
            check = false;
        }

        return check;
    }
}
