package com.example.demo.model;


public class AuthenticationResponse {

	private String token;

	public AuthenticationResponse(String token) {
		this.token = token;
	}
	
	

	public AuthenticationResponse() {
	}



	public String getToken() {
		return token;
	}

	
}
