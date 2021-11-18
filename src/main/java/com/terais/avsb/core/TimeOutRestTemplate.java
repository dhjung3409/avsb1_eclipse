package com.terais.avsb.core;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TimeOutRestTemplate {
	

	public static RestTemplate getHttpsRestTemplate(){
		HttpComponentsClientHttpRequestFactory factory = getHttpFactory();


//		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		DefaultHttpClient httpClient = (DefaultHttpClient) factory.getHttpClient();
		TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] cert, String authType) throws CertificateException {
				return true;
			}
		};
		SSLSocketFactory sf = null;
		try {
			sf = new SSLSocketFactory(acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpClient.getConnectionManager().getSchemeRegistry()
					.register(new Scheme("https", Integer.parseInt(PropertiesData.port), sf));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		}

		RestTemplate rest = new RestTemplate(factory);
		return rest;
	}

	public static RestTemplate getHttpRestTemplate(){
		HttpComponentsClientHttpRequestFactory factory = getHttpFactory();
		RestTemplate rest = new RestTemplate(factory);
		return rest;
	}

	public static HttpComponentsClientHttpRequestFactory getHttpFactory(){
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(2000);
		factory.setReadTimeout(2000);
		return factory;
	}
}
