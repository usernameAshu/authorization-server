package com.ibm.app.security.providers;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;

import com.ibm.app.security.authenticationToken.AddClientUserAuthenticationToken;

@Component
public class AddClientUserAuthenticationProvider implements AuthenticationProvider {

	private PasswordEncoder passwordEncoder;
	private JdbcUserDetailsManager userDetailsManager;
	
	public AddClientUserAuthenticationProvider(PasswordEncoder passwordEncoder,
			JdbcUserDetailsManager userDetailsManager) {
		super();
		this.passwordEncoder = passwordEncoder;
		this.userDetailsManager = userDetailsManager;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		if (supports(authentication.getClass())) {
			return null;
		}
		
		String userNameInput = authentication.getName();
		String passwordInput = (String)authentication.getCredentials();
		UserDetails userDetailsInDatabase = userDetailsManager.loadUserByUsername(userNameInput);
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) userDetailsInDatabase.getAuthorities();
		GrantedAuthority grantedAuthority = authorities.get(0);
		String authority = grantedAuthority.getAuthority();
		
		if( passwordEncoder.matches(passwordInput, userDetailsInDatabase.getPassword())
				&& authority.equalsIgnoreCase("ROLE_ADMIN")) {
			return new AddClientUserAuthenticationToken(userNameInput, passwordInput,
					userDetailsInDatabase.getAuthorities());
		}
		
		throw new BadCredentialsException("Cannot authenticate the given User details. Check user details & privileges");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(AddClientUserAuthenticationToken.class) ;
	}

}
