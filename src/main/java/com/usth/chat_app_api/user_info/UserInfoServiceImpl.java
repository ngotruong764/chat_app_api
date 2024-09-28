package com.usth.chat_app_api.user_info;

import com.usth.chat_app_api.user_login.IUserLoginService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements IUserLoginService {
    private UserInfoRepository repo;
}
