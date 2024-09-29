package com.usth.chat_app_api.filter;

import com.usth.chat_app_api.constant.ApplicationConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt_token = request.getHeader(ApplicationConstant.JWT_HEADER);
    if(jwt_token != null){
        Environment env = getEnvironment();
        if(env != null){
            String secretKey = env.getProperty(ApplicationConstant.JWT_SECRET_KEY,
                    ApplicationConstant.JWT_SECRET_DEFAULT_VALUE);
            SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            if(key != null){
                try{
                    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(jwt_token).getPayload();
                    String username = claims.get("username").toString();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e){
                    throw new BadCredentialsException("Invalid Token received!");
                }
            }
        }
    }
    filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals("/api/v1/user-login/login");
    }
}
