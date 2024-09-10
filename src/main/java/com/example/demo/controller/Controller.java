package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.User;
import com.example.demo.service.AuthenticationService;

@RestController
public class Controller {
 
	
	 //to handle the login request we need to create a authentication service
	private final AuthenticationService authenticationService;

	public Controller(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	
	 @PostMapping("/register")
	 public ResponseEntity<AuthenticationResponse> register(@RequestBody User request) {
		 return ResponseEntity.ok(authenticationService.register(request));
	 }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
 