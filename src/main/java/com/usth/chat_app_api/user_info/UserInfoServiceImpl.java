package com.usth.chat_app_api.user_info;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class UserInfoServiceImpl implements IUserInfoService {
    @Autowired
    private UserInfoRepository repo;

    @Override
    public UserInfo findUserInforById(Long id) {
        return repo.findUserInfoById(id);
    }

    @Override
    public Optional<UserInfo> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public UserInfo saveUserInfo(UserInfo userInfo) {
        return repo.save(userInfo);
    }


    @Override
    public Optional<UserInfo> findByUsername(String loginName) {
        return repo.findByUsername(loginName);
    }

    @Override
    public Optional<UserInfo> findByEmailAndIsActive(String email, boolean isActive) {
        return repo.findByEmailAndIsActive(email, isActive);
    }

    @Override
    public Optional<UserInfo> findByUsernameAndIsActive(String username, boolean isActive) {
        return Optional.empty();
    }


    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }

    @Override
    public Optional<UserInfo> findByVerificationCode(String verificationCode) {
        return repo.findByVerificationCode(verificationCode);
    }

    @Override
    public void deleteVerificationCode(String verificationCode) {
        repo.deleteVerificationCode(verificationCode);
    }
}
