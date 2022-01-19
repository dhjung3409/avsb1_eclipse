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
import java.util.concurrent.TimeoutException;

/**
  * Rest 요청을 보내는 클래스
  */
public class RestURI {

	private static final Logger logger = LoggerFactory.getLogger(RestURI.class);

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
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            input=con.getInputStream();
        } catch (IOException e) {
            logger.error("HTTP Connection IOException: "+url);
            logger.error("Error Message: "+e.getMessage());
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
            KeyStore key = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream("./tomcat/conf/.teraKey");
            key.load(fis,"teralab".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(key);
            SSLContext sc =SSLContext.getInstance("TLS");
            sc.init(null,tmf.getTrustManagers(),new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostVerifier());
            con = (HttpsURLConnection) webURL.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(1000);
            input=con.getInputStream();
        }catch(ConnectException e){
            logger.error("HTTPS Connection NotConnect: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        }catch(SSLHandshakeException e){
            logger.error("HTTPS Connection SSLHandshake Error: "+e.getMessage());
            logger.error("Error Message: "+e.getMessage());
        }catch(SSLException e){
            logger.error("HTTPS Connection SSLException: "+e.getMessage());
            logger.error("Error Message: "+e.getMessage());
        }catch(IOException e) {
            logger.error("HTTPS Connection IOException Error: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        }catch (NoSuchAlgorithmException e) {
            logger.error("HTTPS Connection NoSuchAlgorithmException Error: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        } catch (KeyManagementException e) {
            logger.error("HTTPS Connection KeyManagementException Error: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        } catch (KeyStoreException e) {
            logger.error("HTTPS Connection KeyStoreException Error: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        } catch (CertificateException e) {
            logger.error("HTTPS Connection CertificateException Error: "+webURL);
            logger.error("Error Message: "+e.getMessage());
        }

        return input;
    }




}
