package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.demo.filter.JwtAuthenticationFilter;
import com.example.demo.service.UserDetailsImp;

@Configuration
@EnableWebSecurity
public class Securityconfig {
	
	private final UserDetailsImp userDetailsImp;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	private final CustomLogoutHandler logoutHandler;

	public Securityconfig(CustomLogoutHandler logoutHandler, JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsImp userDetailsImp) {
		this.logoutHandler = logoutHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.userDetailsImp = userDetailsImp;
	}


	@Bean
	public SecurityFilterChain sf(HttpSecurity http) throws Exception
	{
		return http.
				csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(
						//req->req.requestMatchers("/login/**)","/register/**")
						req->req.requestMatchers("/login/**","/register/**")
								.permitAll()
								.anyRequest()
								.authenticated()
						).userDetailsService(userDetailsImp)
				.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class)
				.logout(l->l
						.logoutUrl("/logout")
						.addLogoutHandler(logoutHandler)
						.logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()))
				.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
	{
		return authenticationConfiguration.getAuthenticationManager();
	}

}
