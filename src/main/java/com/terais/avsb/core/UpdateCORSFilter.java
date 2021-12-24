//package com.terais.avsb.core;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
//  * @FileName : UpdateCORSFilter.java
//  * @Project : AVSB1
//  * @Date : 2021. 12. 17.
//  * @작성자 : DooHee Jung
//  * @변경이력 : None
//  * @프로그램 설명 :
//  */
//public class UpdateCORSFilter implements Filter {
//
//    /**
//      * @Method Name : doFilter
//      * @작성일 : 2021. 12. 17.
//      * @작성자 : DooHee Lee
//      * @변경이력 : None
//      * @Method 설명 :
//      * @param req
//      * @param res
//      * @param chain
//      */
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        response.setHeader("Access-Control-Allow-Methods", "POST");
//
//        response.setHeader("Access-Control-Allow-Origin", "*");
//
//
//        try {
//            chain.doFilter(req, res);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ServletException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//      * @Method Name : init
//      * @작성일 : 2021. 12. 17.
//      * @작성자 : DooHee Lee
//      * @변경이력 : None
//      * @Method 설명 :
//      * @param filterConfig
//      * @throws ServletException
//      */
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//    public void destroy() {
//
//    }
//}
