package com.usth.chat_app_api.security_config;

import com.usth.chat_app_api.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    TalkieUserDetailService talkieUserDetailService;
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(request -> {

//            request.requestMatchers("/api/v1/user-login/**").permitAll();
//            request.requestMatchers("/**").permitAll();

            request.requestMatchers("/api/v1/user-info/register").permitAll();
            request.requestMatchers("/api/v1/user-info/test").permitAll();
            request.requestMatchers("/api/v1/user-info/confirm-account").permitAll();
            request.requestMatchers("/api/v1/user-info/login").permitAll();
            request.requestMatchers("/api/v1/user-info/pushVerificationCode").permitAll();
            request.requestMatchers("/api/v1/chat/**").permitAll();
            request.requestMatchers("/**").permitAll();
            request.anyRequest().authenticated();
        });

        // disable csrf
        http.csrf(AbstractHttpConfigurer::disable);
        // making application stateless
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // disable form login
        http.formLogin(AbstractHttpConfigurer::disable);
        //
//        huy mới tắt tạm cái jwt để test endpoint
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // logout
//        http.logout((logout) -> logout.logoutUrl("/api/v1/user-info/logout")
//                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        // use default bcrypt
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //  sets the new strategy to perform the authentication.
    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(talkieUserDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://10.0.2.2:8081", "ws://10.0.2.2:8081/talkie/api/v1/chat","ws://10.0.2.2:8081", "http://localhost:8081", "*"));
//        huy mới theem dòng dưới ể test
//        configuration.setAllowedOrigins(List.of("http://localhost:8080"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        // allow all header
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
