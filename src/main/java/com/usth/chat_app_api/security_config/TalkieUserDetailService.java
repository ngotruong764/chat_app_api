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
import java.util.Optional;

@Service
public class TalkieUserDetailService implements UserDetailsService {
    @Autowired
    private UserLoginRepository repo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean isActive = true;
        Optional<UserLogin> userLogin = repo.findByEmailAndIsActive(username, isActive);
        // Empty authorities
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(userLogin.isPresent() ){
            return new User(userLogin.get().getEmail(), userLogin.get().getPassword(), authorities);
        } else{
            UserLogin login = repo.findByLoginNameAndIsActive(username, isActive)
                    .orElseThrow(() -> new UsernameNotFoundException("User detail not found for the user:" + username));;
            return new User(login.getLoginName(), login.getPassword(), authorities);
        }
    }
}
