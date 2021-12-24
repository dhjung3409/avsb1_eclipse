package com.terais.avsb.service;

import java.util.List;

import com.terais.avsb.vo.LoginVO;

/**
  * @FileName : AccountService.java
  * @Project : AVSB1
  * @Date : 2021. 12. 17.
  * @작성자 : DooHee Jung
  * @변경이력 : None
  * @프로그램 설명 :
  */
public interface AccountService {
	public LoginVO createAccount(String userId,String userPw, String userName, String userRole);
	public boolean checkIDOverlap(List<LoginVO> list, String userId);
	public LoginVO editAccount(LoginVO user,boolean status,String method);
	public void delAccount(List<String> no);
	public List<Object> viewAccountList(String myAccount);
	public List<Object> addAccount(LoginVO account);
}
