package com.terais.avsb.service;

import com.terais.avsb.dto.ResultValue;


public interface ModuleService {
	ResultValue getTotal(String fileName, ResultValue rv);
}
