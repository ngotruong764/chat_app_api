package com.usth.chat_app_api.user_info;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements IUserInfoService {
    @Autowired
    private UserInfoRepository repo;

    @Override
    public UserInfo findUserInforById(Long id) {
        return repo.findUserInfoById(id);
    }
}
