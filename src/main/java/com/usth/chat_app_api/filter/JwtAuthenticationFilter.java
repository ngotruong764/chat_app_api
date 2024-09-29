package com.usth.chat_app_api.filter;

import com.usth.chat_app_api.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try{
            final String jwt_token = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt_token);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(userEmail != null && authentication == null){
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                if(jwtService.isTokenValid(jwt_token, userDetails)){
                    authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch(Exception e){
            throw new BadCredentialsException("Invalid Token received!");
        }
        filterChain.doFilter(request,response);
    }
}
