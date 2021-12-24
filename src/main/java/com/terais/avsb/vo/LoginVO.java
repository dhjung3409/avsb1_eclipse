package com.terais.avsb.vo;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
  * 계정 정보 VO
  */
public class LoginVO implements UserDetails{

	private static final long serialVersionUID = 4014438683256086864L;

	/**
	 * 계정 고유 번호
	 */
	@JsonProperty
	private long no;

	/**
	 * 계정 아이디
	 */
	@JsonProperty
	private String userId;

	/**
	 * 계정 패스워드
	 */
	@JsonProperty
	private String userPw;

	/**
	 * 계정 이름
	 */
	@JsonProperty
	private String userName;

	/**
	 * 계정 권한
	 */
	@JsonProperty
	private String userRole;

	public long getNo() {
		return no;
	}

	public void setNo(long no) {
		this.no = no;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserPw() {
		return userPw;
	}

	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	@Override
	public String toString() {
		return "LoginVO [no=" + no + ", userId=" + userId + ", userPw=" + userPw + ", userName=" + userName
				+ ", userRole=" + userRole + "]";
	}
	
	

	public Collection<? extends GrantedAuthority> getAuthorities() {
		ArrayList<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		auth.add(new SimpleGrantedAuthority("Role_"+userRole));
		return auth;
	}
	
	public String getPassword() {
		return userPw;
	}
	public String getUsername() {
		return userId;
	}
	public boolean isAccountNonExpired() {
		return true;
	}
	public boolean isAccountNonLocked() {
		return true;
	}
	public boolean isCredentialsNonExpired() {
		return true;
	}
	public boolean isEnabled() {
		return true;
	}

	
}
