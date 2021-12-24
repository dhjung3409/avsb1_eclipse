package com.terais.avsb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
  * 검사 결과를 저장하는 클래스
  */
public class ScanResult {
	/**
	 * 스케줄러 스캔 검사 고유번호
	 */
	@JsonProperty
	private long no;

	/**
	 * 스케줄러 스캔 검사 시간
	 */
	@JsonProperty
	private String date;

	/**
	 * 스케줄러 스캔 검사 파일 경로
	 */
	@JsonProperty
	private String filepath;

	/**
	 * 스케줄러 스캔 검사 악성코드 이름
	 */
	@JsonProperty
	private String name;

	/**
	 * 스케줄러 스캔 검사 파일 크기
	 */
	@JsonProperty
	private String size;

	/**
	 * 스케줄러 스캔 검사 결과
	 */
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
