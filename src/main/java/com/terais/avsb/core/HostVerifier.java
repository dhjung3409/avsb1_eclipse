package com.terais.avsb.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * 신뢰 할 수 있는 SSL 인증서 확인 클래스
 */
public class HostVerifier implements HostnameVerifier {

    private static final Logger logger = LoggerFactory.getLogger(HostVerifier.class);

    /**
     * 포트 확인으로 신뢰 여부 구분
     * @param s 도메인 이름
     * @param sslSession SSL 세션 정보
     * @return 신뢰성 여부
     */
    public boolean verify(String s, SSLSession sslSession) {
        boolean portCorrect = false;
        if(sslSession.getPeerPort()==Integer.parseInt(PropertiesData.port)){
            portCorrect = true;
        }

        return portCorrect;
    }
}
