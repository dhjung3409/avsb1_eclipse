package com.terais.avsb.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

public class ResultValue {
	@JsonProperty
	int normal;
	@JsonProperty
	int infected;
	@JsonProperty
	int disinfected;
	@JsonProperty
	int failed;
	@JsonProperty
	int deleteFail;
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
