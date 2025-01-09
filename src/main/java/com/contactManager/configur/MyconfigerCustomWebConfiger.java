package com.contactManager.configur;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MyconfigerCustomWebConfiger {
	
	@Bean// Bean for UserDetailsService
	protected CustomUserDetailsServiceImpl customUserDetailsServiceImpl() {
		
		return new CustomUserDetailsServiceImpl();
	}
	
	@Bean	//Bean for Password Encoder
	protected BCryptPasswordEncoder bCryptPasswordEncoder() {
		
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	protected DaoAuthenticationProvider daoAuthenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(customUserDetailsServiceImpl());
		provider.setPasswordEncoder(bCryptPasswordEncoder());
		return provider;
	}
	
	protected void configur(AuthenticationManagerBuilder builder) throws Exception{
		
		builder.authenticationProvider(daoAuthenticationProvider());
	}
	
	
	@Bean
	protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	    	.csrf(csr ->csr
	    			.disable()
	    			)
	        .authorizeHttpRequests(authorize -> authorize
	                .requestMatchers("/admin/**").hasRole("ADMIN")// Allow public access to /admin/*
	                .requestMatchers("/user/**").hasRole("USER")// Allow public access to /user/*
	                .requestMatchers("/**").permitAll()// Require authentication for all other request
	        )
	        .formLogin(form -> form
	                .loginPage("/login")//custom login page
	                .loginProcessingUrl("/dologin")
	                .defaultSuccessUrl("/user/index"))
	               	//.failureForwardUrl("/dologin"))
	       
	        //configure logout functionality
	        .logout(logout ->logout
	        		.logoutUrl("/logout")//Custom logout
	        		.logoutSuccessUrl("/login?logout")//redirect after seccessful logout
	        		.permitAll()
	         
	        );

	    return http.build();
	}

}
