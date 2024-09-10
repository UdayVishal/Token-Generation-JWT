package com.example.demo.service;


import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import com.example.demo.repository.TokenRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	
	// secret key is used to sign the token and validate the token
	
	private final String SECRET_KEY ="80cc29b8f52b5726903faf5e4f07f3d4090f7f655f38e03445d8ff61376ebb1d"; // in google i tries random 256 bit secret key generator


	private final TokenRepository tokenRepository;

	public JwtService(TokenRepository tokenRepository) {
		this.tokenRepository = tokenRepository;
	}

	public <T> T extractClaim(String token, Function<Claims, T> resolver) // this method is used to extract a particular claim
	{
		Claims claims =	extractAllClaims(token);
		return resolver.apply(claims);
	}
 	
	public String extractUsername(String token) // 3rd Step this is used to get the username from the claims
	{
		return extractClaim(token, Claims::getSubject);
	}
	
	public boolean isValid(String token, UserDetails user) //4th step to check the validity of the token
	{
		String username = extractUsername(token);

		// this is actually used to invalidate the token after it is being set as is_logged_out as true.
		boolean isValidtoken = tokenRepository.findByAccessToken(token).map(t->!t.isLoggedOut()).orElse(false);
		return (username.equals(user.getUsername())) && !isTokenExpired(token) && isValidtoken;
		
	}
	
	private boolean isTokenExpired(String token) {  //5th step to check the token is expired or not
		// TODO Auto-generated method stub
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token, Claims::getExpiration);
	}

	private Claims extractAllClaims(String token) {  // 2nd Step to get all the claims
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
	public String generateToken(User user) // 1st Step to generate the token we need a method
	{
		String token = Jwts
				.builder()
				.subject(user.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+24*60*60*1000))
				.signWith(getSigninKey())
				.compact();
		
		return token;	
	}

	private SecretKey getSigninKey() {  // this methods is used to decode the secret key and it should be private
		// TODO Auto-generated method stub
		byte[] keyBytes= Decoders.BASE64URL.decode(SECRET_KEY); // after that we need to decode the secret key we intialized
        return Keys.hmacShaKeyFor(keyBytes);	// hmac is an hashing fucntion
	}
}
