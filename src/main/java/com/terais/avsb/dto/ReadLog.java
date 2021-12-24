package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
  * 로그 정보를 저장하는 DTO 클래스
  */
public class ReadLog {
	/**
	 * 로그 고유 번호
	 */
	@JsonProperty
	private long no;

	/**
	 * 파일 검사 시간
	 */
	@JsonProperty
	private String date;

	/**
	 * 파일 검사 결과
	 */
	@JsonProperty
	private String result;

	/**
	 * 파일 검사가 진행된 IP
	 */
	@JsonProperty
	private String client_ip;

	/**
	 * 검사한 파일 경로
	 */
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
