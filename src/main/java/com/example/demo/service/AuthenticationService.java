package com.example.demo.service;

import com.example.demo.model.Token;
import com.example.demo.repository.TokenRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import java.util.List;

@Service
public class AuthenticationService {

	
	
	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	private final TokenRepository tokenRepository;

	public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, TokenRepository tokenRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.authenticationManager = authenticationManager;
		this.tokenRepository = tokenRepository;
	}

	public AuthenticationResponse register(User request)
	{
		User user = new User();
		user.setFirst_name(request.getFirst_name());
		user.setLast_name(request.getLast_name());
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		user.setRole(request.getRole());
		
		user=userRepository.save(user);
		
		String token= jwtService.generateToken(user);

		//revokeallusertokens(user);
		  // save the generated token in the database created
		extractedUserToken(token, user);

		//SaveUserToken(token, user);
		return new AuthenticationResponse(token);
	}

	private void extractedUserToken(String token, User user) {
		Token token1=new Token();
		token1.setAccessToken(token);
		token1.setUser(user);
		token1.setLoggedOut(false);
		tokenRepository.save(token1);
	}

	public AuthenticationResponse authenticate(User request) {
	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                        request.getUsername(),
	                        request.getPassword()
	                )
	        );
	        
	        System.out.println(request.getUsername());
	        System.out.println(request.getPassword());

	        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
	        String Token = jwtService.generateToken(user);

		// actually used to iterate the tokens and set them as loggedout once authenticate is done
		revokeallusertokens(user);
		extractedUserToken(Token, user);

	        return new AuthenticationResponse(Token);

	    }

	private void revokeallusertokens(User user) {
		List<Token> validTokenbyUserList = tokenRepository.findAllAccessTokensByUser(user.getId());
		if(!validTokenbyUserList.isEmpty()) // if it is not empty
		{
			validTokenbyUserList.forEach(t->{
				t.setLoggedOut(true);
			});
		}

		tokenRepository.saveAll(validTokenbyUserList);
	}

}
