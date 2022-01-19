package com.terais.avsb.web.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.AccountServiceImpl;
import com.terais.avsb.vo.LoginVO;

/**
  * 계정관리 관련 컨트롤러
  */
@Controller
@RequestMapping("account/*")
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	/**
	 * bean 등록된 AccountServiceImpl 클래스 객체
	 */
	@Autowired
	private AccountServiceImpl accountService;
	

	/**
	  * 계정관리 페이지 뷰 포인트
	  * @return 계정관리 페이지
	  */
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public ModelAndView accountHome(){
		logger.debug("account view");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/account");
		mav.addObject("message","error");
		
		return mav;
	}

	/**
	  * 계정 삭제
	  * @param delItems 삭제할 계정 고유 번호 리스트
	  */
	@RequestMapping(value="/userdel",method=RequestMethod.POST)
	@ResponseBody
	public void removeUser(@RequestParam(value="items[]") List<String> delItems){

		logger.debug("delList : "+delItems);
		accountService.delAccount(delItems);
	}

	/**
	  * 계정 추가
	  * @param userId 계정 아이디
	  * @param userPw 계정 패스워드
	  * @param userRole 계정 권한
	  * @param userName 계정 이름
	  * @return 계정관리 페이지
	  */
	@RequestMapping(value="/useradd",method=RequestMethod.POST)
	@ResponseBody
	public ModelAndView addUser(@RequestParam ("userId")String userId,
						@RequestParam ("userPw")String userPw,
						@RequestParam ("userRole")String userRole,
						@RequestParam ("userName")String userName){

		ModelAndView mav = new ModelAndView();

		accountService.createAccount(userId, userPw, userName, userRole);
		logger.debug("userId : "+userId);
		logger.debug("userPw : "+userPw);
		logger.debug("userRole : "+userRole);
		logger.debug("userName : "+userName);

		mav.setViewName("redirect:/account/view");

		return mav;
	}


	/**
	  * 계정 목록
	  * @param principal 로그인한 계정 정보
	  * @return 계정 목록
	  */
	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getAccountList(Principal principal){
		List<Object> accountList = accountService.viewAccountList(principal.getName().toString());
		return accountList;
	}


	/**
	  * 계정 수정
	  * @param userCount 수정 할 계정 고유 번호
	  * @param status 계정 패스워드 수정 유무
	  * @param userPw 계정 패스워드
	  * @param userName 계정 이름
	  * @param userRole 계정 역할
	  * @return 수정한 계정 정보
	  */
	@RequestMapping(value="/mod",method=RequestMethod.POST)
	@ResponseBody
	public LoginVO editAccountDetail(@RequestParam long userCount,
									 @RequestParam boolean status,
									 @RequestParam String userPw,
									 @RequestParam String userName,
									 @RequestParam String userRole){
		LoginVO loginVO = new LoginVO();
		loginVO.setNo(userCount);
		loginVO.setUserPw(userPw);
		loginVO.setUserName(userName);
		loginVO.setUserRole(userRole);
		logger.debug(loginVO.toString()+": no= "+loginVO.getNo());
		LoginVO account=accountService.editAccount(loginVO,status,"POST");
		
		return account;
	}

	/**
	  * 로그인한 계정 패스워드 변경
	  * @param nowPass 현재 패스워드
	  * @param newPass 바꿀 패스워드
	  * @param prin 로그인한 계정 정보
	  * @return 패스워드 변경 성공, 실패 여부
	  */
	@RequestMapping(value = "/password/reset",method = RequestMethod.POST)
	@ResponseBody
	public boolean resetPassword(@RequestParam String nowPass, @RequestParam String newPass, Principal prin){
		String id = prin==null?null:prin.getName();
		return accountService.changePassword(id,nowPass,newPass);
	}
}



