package com.terais.avsb.service;

import java.util.List;

import com.terais.avsb.vo.LoginVO;

public interface AccountService {
	public LoginVO createAccount(String userId,String userPw, String userName, String userRole);
	public boolean checkIDOverlap(List<LoginVO> list, String userId);
	public LoginVO editAccount(LoginVO user,boolean status,String method);
	public void delAccount(List<String> no);
	public List<Object> viewAccountList(String myAccount);
	public List<Object> addAccount(LoginVO account);
}
