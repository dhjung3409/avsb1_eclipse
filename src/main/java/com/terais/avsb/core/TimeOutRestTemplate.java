package com.terais.avsb.core;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class TimeOutRestTemplate {
	

	public static RestTemplate getRestTemplate(){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(2000);
		factory.setReadTimeout(2000);
		RestTemplate rest = new RestTemplate(factory);
		return rest;
	}
}
