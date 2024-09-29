package com.usth.chat_app_api.security_config;

import com.usth.chat_app_api.user_login.UserLogin;
import com.usth.chat_app_api.user_login.UserLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TalkieUserDetailService implements UserDetailsService {
    @Autowired
    private UserLoginRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserLogin userLogin = repo.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User detail not found for the user:" + username));
        // Empty authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        //
        return new User(userLogin.getEmail(), userLogin.getPassword(), authorities);
    }
}
