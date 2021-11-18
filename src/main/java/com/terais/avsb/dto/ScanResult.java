package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScanResult {
	@JsonProperty
	private long no;
	@JsonProperty
	private String date;
	@JsonProperty
	private String filepath;
	@JsonProperty
	private String name;
	@JsonProperty
	private String size;
	@JsonProperty
	private String result;
	
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
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "ScanResult [no=" + no + ", date=" + date + ", filepath=" + filepath + ", name=" + name + ", size="
				+ size + ", result=" + result + "]";
	}
	
	
}
