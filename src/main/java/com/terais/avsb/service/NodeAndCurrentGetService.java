package com.terais.avsb.service;

import java.util.List;
import java.util.Map;

/**
  *
  */
public interface NodeAndCurrentGetService {
    public String getRest(String ip, String select, String option);
    public Map<Object,Object> getNode(String period);
    public List<Object> getChart();
}
