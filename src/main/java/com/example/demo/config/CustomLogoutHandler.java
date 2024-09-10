package com.example.demo.config;

import com.example.demo.model.Token;
import com.example.demo.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authheader= request.getHeader("Authorization");

        if(authheader == null || !authheader.startsWith("Bearer ")) // check if it null
        {
            return;
        }

        String token= authheader.substring(7);

        Token storedtoken = tokenRepository.findByAccessToken(token).orElse(null);

        if(token!=null)
        {
            storedtoken.setLoggedOut(true);
            tokenRepository.save(storedtoken);
        }

    }
}
