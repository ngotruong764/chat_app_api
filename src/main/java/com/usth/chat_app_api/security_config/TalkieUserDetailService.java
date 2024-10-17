package com.usth.chat_app_api.security_config;

import com.usth.chat_app_api.user_info.UserInfo;
import com.usth.chat_app_api.user_info.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class TalkieUserDetailService implements UserDetailsService {
    @Autowired
    private UserInfoRepository repo;

    
    @Override
    public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {
        boolean isActive = true;
        Optional<UserInfo> userLogin = repo.findByEmailOrUsernameAndIsActive(username, isActive);
        if(userLogin.isEmpty()){
            throw new UsernameNotFoundException("User detail not found for the user:" + username);
        }
        return userLogin.get();
    }

}
