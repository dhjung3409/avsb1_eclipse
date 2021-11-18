package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReadLog {
	@JsonProperty
	private long no;
	@JsonProperty
	private String date;
	@JsonProperty
	private String result;
	@JsonProperty
	private String client_ip;
	@JsonProperty
	private String target;


	@Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof ReadLog)
        {        	
            sameSame = this.target.equals(((ReadLog) object).target) &&
            		this.date.equals(((ReadLog) object).date);
            
        }

        return sameSame;
    }

	public long getNo() {
		return no;
	}
	public void setNo(long no) {
		this.no = no;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return "ReadLog [no=" + no + ", date=" + date + ", result=" + result + ", client_ip=" + client_ip + ", target="
				+ target + "]";
	}
		
}
