package com.terais.avsb.dto;

public class ReadLog {
	private long no;
	private String date;
	private String result;
	private String client_ip;
	private String target;

	public ReadLog(){
		
	}

	public ReadLog(long no, String date, String result, String client_ip, String target) {
		this.no = no;
		this.date = date;
		this.result = result;
		this.client_ip = client_ip;
		this.target = target;



	}	
	
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
