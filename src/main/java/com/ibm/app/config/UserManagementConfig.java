package com.ibm.app.config;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.ibm.app.security.filters.AddClientUserDetailsFilter;
import com.ibm.app.security.providers.AddClientUserAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class UserManagementConfig extends WebSecurityConfigurerAdapter {
	
	private DataSource datasource;
	private AddClientUserDetailsFilter clientUserDetailsFilter;
	private AddClientUserAuthenticationProvider clientUserAuthenticationProvider;
	
	public UserManagementConfig(DataSource datasource, @Lazy AddClientUserDetailsFilter clientUserDetailsFilter,
			@Lazy AddClientUserAuthenticationProvider clientUserAuthenticationProvider) {
		super();
		this.datasource = datasource;
		this.clientUserDetailsFilter = clientUserDetailsFilter;
		this.clientUserAuthenticationProvider = clientUserAuthenticationProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		http.addFilterAt(clientUserDetailsFilter, BasicAuthenticationFilter.class);
		
		//Form Login doesn't work for adding client or user details in DB from SoapUI
		//added custom filters for Admin operations 
		http.authorizeRequests()
		.antMatchers("/welcome/**")
		.hasAnyRole("USER", "ADMIN")
		.and()
		.formLogin()
		.and()
		.authorizeRequests()
		.antMatchers("/addClient/**","/addUser/**")
		.hasRole("ADMIN")
		.and()
		.csrf().disable();
		
		
		//Working code, skips the form login page
		//addClient & addUser is working 
//		http.authorizeRequests()
//		.antMatchers("/addClient/**", "/addUser/**", "/welcome/**")
//		.permitAll()
//		.anyRequest()
//		.authenticated();
//		http.formLogin();
//		http.csrf().disable();
		
		//Example code
		//http.authorizeRequests().antMatchers("/**").hasRole("USER").and().formLogin();
		
	}
	
	/**
	 * To resolve the issue of generating refresh_token 
	 * There is a "local" AuthenticationManagerBuilder and a "global" AuthenticationManagerBuilder 
	 * and we need to set it on the global version in order 
	 * to have this information passed to these other builder contexts.
	 * 
	 */
	@Autowired
	public void setApplicationContext(ApplicationContext context) {
	    super.setApplicationContext(context);
	    AuthenticationManagerBuilder globalAuthBuilder = context
	            .getBean(AuthenticationManagerBuilder.class);
	    try {
	        globalAuthBuilder.userDetailsService(userDetailsManager());
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * To enable user and client authentication via database
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.dataSource(datasource)
		.and()
		.authenticationProvider(clientUserAuthenticationProvider)
		.userDetailsService(userDetailsManager());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Bean
	public JdbcUserDetailsManager userDetailsManager() {
		return new JdbcUserDetailsManager(datasource);
	}
	
}
