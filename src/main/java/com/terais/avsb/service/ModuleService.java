package com.terais.avsb.service;

import com.terais.avsb.dto.ResultValue;


/**
  * @FileName : ModuleService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface ModuleService {
	ResultValue getTotal(String fileName, ResultValue rv);
}
