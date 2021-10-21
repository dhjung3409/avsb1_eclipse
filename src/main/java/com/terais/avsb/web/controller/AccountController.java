package com.terais.avsb.web.controller;

import java.security.Principal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.terais.avsb.service.impl.AccountServiceImpl;
import com.terais.avsb.vo.LoginVO;

@Controller
@RequestMapping("account/*")
public class AccountController {

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private AccountServiceImpl accountService;
	

	@RequestMapping(value="/view", method=RequestMethod.GET)
	public ModelAndView accountHome(){
		logger.debug("account view");
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("page/account");
		mav.addObject("message","error");
		
		return mav;
	}


	@RequestMapping(value="/shutdown",method=RequestMethod.GET)
	@ResponseBody
	public void systemShutdown(){
		logger.debug("this gonna be shutdown");
		System.exit(-1);
		logger.debug("this gonna be shutdown successfully");
	}


	@RequestMapping(value="/test",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getAccounts(Principal prin){

		String name = prin!=null?prin.getName():null;
		List<Object> accountList = accountService.viewAccountList(name);
		return accountList;
	}

	@RequestMapping(value="/userdel",method=RequestMethod.POST)
	@ResponseBody
	public void removeUser(@RequestParam(value="items[]") List<String> delItems){

		logger.debug("delList : "+delItems);
		accountService.delAccount(delItems);
	}

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


	@RequestMapping(value="/list",method=RequestMethod.GET)
	@ResponseBody
	public List<Object> getAccountList(Principal principal){
		String name = principal!=null?principal.getName():null;
		List<Object> accountList = accountService.viewAccountList(name);
		return accountList;
	}


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

	@RequestMapping(value="/{accountNo}",method=RequestMethod.DELETE)
	@ResponseBody
	public LoginVO eraseAccountDetail(@PathVariable("accountNo") long accountNo){
		LoginVO vo = new LoginVO();
		vo.setNo(accountNo);
		LoginVO account=accountService.editAccount(vo, false, "DELETE");
		
		return account;
	}

	@RequestMapping(value = "/password/reset",method = RequestMethod.POST)
	@ResponseBody
	public boolean resetPassword(@RequestParam String nowPass, @RequestParam String newPass, Principal prin){
		String id = prin==null?null:prin.getName();
		return accountService.changePassword(id,nowPass,newPass);
	}
}



