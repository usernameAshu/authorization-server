package com.ibm.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.app.dto.UserDetailsRequestDto;

@RestController
public class AuthServletController {
	
	private JdbcClientDetailsService clientDetailsService;
	private PasswordEncoder passwordEncoder;
	private JdbcUserDetailsManager userDetailsManager;
	
	public AuthServletController(
			JdbcClientDetailsService clientDetailsService, 
			PasswordEncoder passwordEncoder,
			JdbcUserDetailsManager userDetailsManager) {
		super();
		this.clientDetailsService = clientDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsManager = userDetailsManager;
	}

	private static final Log LOG = LogFactory.getLog(AuthServletController.class);
	
	@PostMapping("/addClient")
	public void addClientDetails(@RequestBody BaseClientDetails clientDetails, Authentication authentication) {
		if(authentication != null) {
			LOG.info("Authentication details: " + authentication.getName());
		} else {
			LOG.info("addUser : Authentication is NULL");
		}
		LOG.info("Base client details : " + clientDetails);
		String secret = clientDetails.getClientSecret();
		clientDetails.setClientSecret(passwordEncoder.encode(secret));

		clientDetailsService.addClientDetails(clientDetails);
	}
	
	@PostMapping("/addUser")
	public void addUser(@RequestBody UserDetailsRequestDto requestDto, Authentication authentication) {
		if(authentication != null) {
			LOG.info("Authentication details: " + authentication.getName());
		} else {
			LOG.info("addUser : Authentication is NULL");
		}
		Set<String> authorities = requestDto.getAuthorities();
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		GrantedAuthority grantedAuthority = null;
		for(String authority : authorities) {
			grantedAuthority = () -> authority;
			grantedAuthorities.add(grantedAuthority);
		}
		User user = new User(
				requestDto.getUsername(), 
				passwordEncoder.encode(requestDto.getPassword()),
				grantedAuthorities);
				
		userDetailsManager.createUser(user);
	}
	
	@GetMapping("/welcome")
	public String welcomeScreen(Authentication authentication) {
		if (authentication != null && authentication.getName() != null)
			return "welcome " + authentication.getName();
		else
			return "welcome anonymous user";
	}
	
}











