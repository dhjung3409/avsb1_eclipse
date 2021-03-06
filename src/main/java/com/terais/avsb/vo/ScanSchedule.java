package com.terais.avsb.vo;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.terais.avsb.core.SimpleDateFormatCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;


/**
  * 스케줄러 검사 VO
  */
public class ScanSchedule implements Comparable<ScanSchedule>, Comparator<ScanSchedule> {

	private static final Logger logger = LoggerFactory.getLogger(ScanSchedule.class);

	/**
	 * 검사 고유 번호
	 */
	@JsonProperty
	private long no;

	/**
	 * 검사 등록 시간
	 */
	@JsonProperty
	private String reservationDate;

	/**
	 * 검사 시작 시간
	 */
	@JsonProperty
	private String startDate;

	/**
	 * 검사 종료 시간
	 */
	@JsonProperty
	private String endDate;

	/**
	 * 검사 Task ID
	 */
	@JsonProperty
	private String taskID;

	/**
	 * 검사 유형
	 */
	@JsonProperty
	private String type;

	/**
	 * 검사 경로
	 */
	@JsonProperty
	private String path;

	/**
	 * 검사 결과물 경로
	 */
	@JsonProperty
	private String result;

	/**
	 * 검사 상황
	 */
	@JsonProperty
	private String report;

	/**
	 * 검사 주기
	 */
	@JsonProperty
	private String cycle;

	public long getNo() {
		return no;
	}

	public void setNo(long no) {
		this.no = no;
	}

	public String getReservationDate() {
		return reservationDate;
	}

	public void setReservationDate(String reservationDate) {
		this.reservationDate = reservationDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		this.cycle = cycle;
	}

	@Override
	public String toString() {
		return "ScanSchedule{" +
				"no=" + no +
				", reservationDate='" + reservationDate + '\'' +
				", startDate='" + startDate + '\'' +
				", endDate='" + endDate + '\'' +
				", taskID='" + taskID + '\'' +
				", type='" + type + '\'' +
				", path='" + path + '\'' +
				", result='" + result + '\'' +
				", report='" + report + '\'' +
				", cycle='" + cycle + '\'' +
				'}';
	}

	public int compareTo(ScanSchedule o) {
		return 0;
	}



	public int compare(ScanSchedule o1, ScanSchedule o2) {
		Date date1 = null;
		Date date2 = null;
		try {
			if(o1.getResult().equals("finish")){
				date1 = SimpleDateFormatCore.sdf.parse(o1.endDate);
				date2 = SimpleDateFormatCore.sdf.parse(o2.endDate);
			}else{
				date1 = SimpleDateFormatCore.sdf.parse(o1.reservationDate);
				date2 = SimpleDateFormatCore.sdf.parse(o2.reservationDate);
			}
		} catch (ParseException e) {
			logger.error("ScanSchedule Compare ParseException: "+e.getMessage());
		}

		return date1.compareTo(date2);
	}
}
