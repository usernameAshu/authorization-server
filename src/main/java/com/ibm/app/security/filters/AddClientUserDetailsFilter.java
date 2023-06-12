package com.ibm.app.security.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AddClientUserDetailsFilter extends OncePerRequestFilter {
	
	private AuthenticationManager authenticationManager;
	
	public AddClientUserDetailsFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}
	
	private static final Log LOG = LogFactory.getLog(AddClientUserDetailsFilter.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// Step 1 : Username & Password authentication
		String authHeader = request.getHeader("Authorization");
		String authType = request.getAuthType();

		Map<String, String> credentialsMap = base64Decoder(authHeader);
		// these parameters should be fetched from Request
		String username = credentialsMap.get("username");
		String password = credentialsMap.get("password");
		
		GrantedAuthority authority = () -> "ROLE_ADMIN";
		List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		grantedAuthorities.add(authority);
		
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, grantedAuthorities);
		
		authenticationManager.authenticate(authentication);
		LOG.info("User is successfully authenticated with ROLE: ADMIN");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
		
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// TODO Auto-generated method stub
		return !(request.getServletPath().equals("/addClient") || request.getServletPath().equals("/addUser"));
	}

	/**
	 * Decoding the "Basic 1234xyz" from auth header into username & password using
	 * base64 {@link Base64.Decoder}
	 * 
	 * @param authHeader
	 * @return
	 */
	private Map<String, String> base64Decoder(String authHeader) {
		byte[] decodeBytes = Base64.getDecoder().decode(authHeader.split(" ")[1]);
		String decodedString = new String(decodeBytes);
		String username = decodedString.substring(0, decodedString.indexOf(":"));
		String password = decodedString.substring(decodedString.indexOf(":") + 1);

		Map<String, String> credentialsMap = new HashMap<>();
		credentialsMap.put("username", username);
		credentialsMap.put("password", password);

		return credentialsMap;

	}
}
