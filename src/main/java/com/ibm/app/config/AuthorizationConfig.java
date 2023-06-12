package com.ibm.app.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;

@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {
	
	private final AuthenticationManager authenticationManager;
	private DataSource dataSource;
	
	public AuthorizationConfig(AuthenticationManager authenticationManager, DataSource datasource) {
		this.authenticationManager = authenticationManager;
		this.dataSource = dataSource;
	}
	
//	@Bean
//	public AccessDecisionVoter<FilterInvocation> accessDecisionVoter() {
//		return new RoleVoter();
//	}
	
//	@Bean
//	public AffirmativeBased affirmativeBased() {
//		List<AccessDecisionVoter<?>> accessDecisionVoters = new ArrayList<>();
//		RoleVoter voter = new RoleVoter();
//		accessDecisionVoters.add(voter);
//		
//		return new AffirmativeBased(accessDecisionVoters);
//	}
	
	/**
	 * To configure the clients in the authorization server 
	 */
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients.inMemory()
//			.withClient("client1")
//			.secret("secret")
//			.authorizedGrantTypes("password")
//			.scopes("read");
		
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager);
		
	}
	
	/**
	 * This bean creates Clients in the databases
	 * @param dataSource
	 * @return
	 */
	@Bean
	public JdbcClientDetailsService clientDetailsService(DataSource dataSource) {
		return new JdbcClientDetailsService(dataSource);
	}
	
	
}
