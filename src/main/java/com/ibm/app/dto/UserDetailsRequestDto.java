package com.ibm.app.dto;

import java.util.Set;

import lombok.Data;

@Data
public class UserDetailsRequestDto {
	
	private String username;
	private String password;
	private Set<String> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;

}
