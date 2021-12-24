package com.terais.avsb.service;

import java.util.List;

import com.terais.avsb.dto.ReadLog;

/**
  * @FileName : PageLogService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface PageLogService {
	public List<ReadLog> getPageLog();
//	public Object getLogObject(ReadLog readLog);
}
