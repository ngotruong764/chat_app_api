package com.usth.chat_app_api.filter;

import com.usth.chat_app_api.constant.ApplicationConstant;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import io.jsonwebtoken.security.Keys;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            Environment environment = getEnvironment();
            if(environment != null){
                String secret = environment.getProperty(ApplicationConstant.JWT_SECRET_KEY, ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                //create JWT token
                String jwt_token = Jwts.builder().issuer("Talkie")
                        .subject("JWT Token")
                        .claim("username", authentication.getName())
                        .issuedAt(new Date())
                        .expiration(new Date(new Date().getTime() + 3000000))
                        .signWith(secretKey).compact();
                response.setHeader(ApplicationConstant.JWT_HEADER, jwt_token);
            }
        }
        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return !request.getServletPath().equals("/api/v1/user-login/login");
    }
}
