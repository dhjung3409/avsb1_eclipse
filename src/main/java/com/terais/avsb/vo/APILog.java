package com.terais.avsb.vo;

/**
  * APILog VO
  */
public class APILog {
    /**
     *
     */
    private String date;

    /**
     *
     */
    private String client;

    /**
     *
     */
    private String path;

    /**
     *
     */
    private String result;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
        return "APILog{" +
                "date='" + date + '\'' +
                ", client='" + client + '\'' +
                ", path='" + path + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
