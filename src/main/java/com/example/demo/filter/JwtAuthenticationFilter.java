package com.example.demo.filter;

import java.io.IOException;


import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.JwtService;
import com.example.demo.service.UserDetailsImp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	
	private final JwtService jwtService;
	
	private final UserDetailsImp userDetailsservice;
	

	public JwtAuthenticationFilter(JwtService jwtService,UserDetailsImp userDetailsservice) {
		this.jwtService = jwtService;
		this.userDetailsservice = userDetailsservice;
	}


	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String authheader= request.getHeader("Authorization");
		
		if(authheader == null || !authheader.startsWith("Bearer ")) // check if it null
		{
			filterChain.doFilter(request, response);
			return;
		}
		
		String token= authheader.substring(7);
		String username = jwtService.extractUsername(token);
		
		if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null)
		{
			UserDetails userDetails = userDetailsservice.loadUserByUsername(username);
			
			if(jwtService.isValid(token, userDetails))
			{
				UsernamePasswordAuthenticationToken uToken= new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
				uToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(uToken);
				
			}
			
		}
		filterChain.doFilter(request, response);
	}
	
	
	/// after doing this filter we need to register this filter in our spring security

}
