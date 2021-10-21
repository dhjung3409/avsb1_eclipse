package com.terais.avsb.dto;

public class ScanResult {
	private long no;
	private String date;
	private String filepath;
	private String name;
	private String size;
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
