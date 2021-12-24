package com.terais.avsb.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
  * 로그의 검사 결과를 분류해 카운팅하고 저장하는 클래스
  */
public class ResultValue {

	/**
	 * 검사 결과 NORMAL_FILE 카운팅 개수
	 */
	@JsonProperty
	int normal;

	/**
	 * 검사 결과 INFECTED 카운팅 개수
	 */
	@JsonProperty
	int infected;

	/**
	 * 검사 결과 SCAN_CURE_DELETE_SUCCESS 카운팅 개수
	 */
	@JsonProperty
	int disinfected;

	/**
	 * 검사 결과 SCAN_FAILED 카운팅 개수
	 */
	@JsonProperty
	int failed;

	/**
	 * 검사 결과 SCAN_CURE_DELETE_FAIL_BY_CONFIGURE 카운팅 개수
	 */
	@JsonProperty
	int deleteFail;

	/**
	 * 전체 검사 수
	 */
	@JsonProperty
	int total;

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
	public int getDisinfected() {
		return disinfected;
	}
	public void setDisinfected(int disinfected) {
		this.disinfected = disinfected;
	}
	public int getFailed() {
		return failed;
	}
	public void setFailed(int failed) {
		this.failed = failed;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getDeleteFail(){
		return deleteFail;
	}
	public void setDeleteFail(int deleteFail){
		this.deleteFail=deleteFail;
	}
	@Override
	public String toString() {
		return "{'normal'='" + normal + "', 'infected'='" + infected + "', 'disinfected'='" + disinfected + "', 'failed'="
				+ failed + "', 'deleteFail'="
						+ deleteFail +"', 'total'='" + total + "'}";
	}
	public ResultValue(int normal, int infected, int disinfected, int failed,int deleteFail, int total) {
		this.normal = normal;
		this.infected = infected;
		this.disinfected = disinfected;
		this.failed = failed;
		this.deleteFail=deleteFail;
		this.total = total;
		
	}
	public ResultValue() {
		this.normal = 0;
		this.infected = 0;
		this.disinfected = 0;
		this.failed = 0;
		this.deleteFail=0;
		this.total = 0;
	}
	
}
