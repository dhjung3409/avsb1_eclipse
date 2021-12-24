package com.terais.avsb.module;

import com.terais.avsb.core.HostVerifier;
import com.terais.avsb.core.PropertiesData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
  * Rest 요청을 보내는 클래스
  */
public class RestURI {

	private static final Logger logger = LoggerFactory.getLogger(RestURI.class);

//    public static String getRequestUri(RestTemplate rest, String uri){
//        ResponseEntity<String> response=null;
//        String result = null;
//        try{
//            logger.info("URI: "+uri);
//            rest.getRequestFactory();
//            logger.info("getRequestFactory");
//            response = rest.getForEntity(uri, String.class);
//            logger.info("getForEntity");
//            HttpStatus status = response.getStatusCode();
//            logger.info("getHttpStatus");
//            if(status.toString().equals("200")&&response.getBody()!=null){
//                logger.info("RestURI if: "+response.getBody());
//                result= response.getBody().toString();
//
//            }else if (response.getBody()==null) {
//                result="this is null";
//            }else{
//                logger.info("RestURI if else");
//            }
//            logger.info("RestURI end");
//        }catch(Exception e){
//            logger.error("Exception : "+e.getMessage());
//        }
//        logger.info("result: "+result);
//        return result;
//    }

    /**
      * API 요청 URL 연결을 시도하는 메소드
      * @param url 연결해야 하는 URL
      * @return 요청 결과 값
      */
    public static String getRequestURL(String url){
        logger.debug("getRequestURL Test");
        logger.debug("Input URL: "+url);
        String result = "";
        URL webURL=null;
        URLConnection con = null;
        BufferedReader bf = null;
        InputStream input = null;
        try {
            webURL = new URL(url);
//            con = webURL.openConnection();
//
//            if(url.contains("https://")){
//                con.setRequestProperty("User-Agent","Mozilla/5.0");
//            }
//            logger.info("Header Fields: "+con.getHeaderFields().isEmpty());
//            if(con.getHeaderFields().isEmpty()){
//                return null;
//            }
            if(webURL.getProtocol().equals("https")){
                input=getHttpsURLConnection(webURL);
            }else{
                input=getHttpURLConnection(webURL);
            }

            if(input!=null) {
                bf = new BufferedReader(new InputStreamReader(input));
                String inputLine;
                while ((inputLine = bf.readLine()) != null) {
                    result += inputLine;
                }
            }

            if(bf!=null&&result.equals("")){
                result = "this is null";
            }else if(result!=null){

            }else{
                result="";
            }

        } catch (MalformedURLException e) {
            logger.error("getRequestURL MalformedURLException: "+e.getMessage());
            e.printStackTrace();
            result="";
        } catch (IOException e) {
            logger.error("getRequestURL IOException: "+e.getMessage());
            e.printStackTrace();
            result="";
        } catch(Exception e){
            logger.error("getRequestURL Exception: "+e.getMessage());
            e.printStackTrace();
            result="";
        } finally{
            if(bf!=null){
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        logger.debug("result: "+result);
        return result;
    }

    /**
      * HTTP URL 요청하는 메소드
      * @param url 요청해야 하는 URL
      * @return 요청 결과값
      */
    public static InputStream getHttpURLConnection(URL url){
        HttpURLConnection con=null;
        InputStream input = null;
        try {
            con = (HttpURLConnection) url.openConnection();
            input=con.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;

    }
    
    /**
      * HTTPS URL 요청하는 메소드
      * @param webURL 요청해야 하는 URL
      * @return 요청 결과값
      */
    public static InputStream getHttpsURLConnection(URL webURL){
        HttpsURLConnection con=null;
        InputStream input = null;
        try {


//            TrustManager tm = new X509TrustManager() {
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
//                public X509Certificate[] getAcceptedIssuers() {return null;}
//            };
            KeyStore key = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("./tomcat/conf/.teraKey");
            key.load(fis,"teralab".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(key);
//            KeyManagerFactory keyManager = KeyManagerFactory.getInstance("SunX509");
//            keyManager.init(key,"teralab".toCharArray());
            SSLContext sc =SSLContext.getInstance("TLS");
            sc.init(null,tmf.getTrustManagers(),new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostVerifier());
            con = (HttpsURLConnection) webURL.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(1000);
            input=con.getInputStream();
        }catch(ConnectException e){
            logger.error("NotConnect: "+webURL);
        }catch(SSLHandshakeException e){
            logger.error("SSLHandshake Error: "+e.getMessage());
            e.printStackTrace();
        }catch(SSLException e){
            logger.error("SSLException: "+e.getMessage());
        }catch(IOException e) {
            logger.error("IOException Error: "+webURL);
            e.printStackTrace();
        }catch (NoSuchAlgorithmException e) {
            logger.error("NoSuchAlgorithmException Error: "+webURL);
            e.printStackTrace();
        } catch (KeyManagementException e) {
            logger.error("KeyManagementException Error: "+webURL);
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        }

        return input;
    }




}
