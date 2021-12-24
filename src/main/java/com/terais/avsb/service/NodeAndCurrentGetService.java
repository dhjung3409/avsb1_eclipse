package com.terais.avsb.service;

import java.util.List;
import java.util.Map;

/**
  * @FileName : NodeAndCurrentGetService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface NodeAndCurrentGetService {
    public String getRest(String ip, String select, String option);
    public Map<Object,Object> getNode(String period);
    public List<Object> getChart();
}
