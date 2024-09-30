package com.usth.chat_app_api.user_login;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginServiceImpl implements IUserLoginService{
    @Autowired
    private UserLoginRepository repo;

    @Override
    public Optional<UserLogin> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public UserLogin saveUserLogin(UserLogin userLogin) {
        return repo.save(userLogin);
    }


    @Override
    public Optional<UserLogin> findByLoginName(String loginName) {
        return repo.findByLoginName(loginName);
    }

    @Override
    public Optional<UserLogin> findByEmailAndIsActive(String email, boolean isActive) {
        return repo.findByEmailAndIsActive(email, isActive);
    }

    @Override
    public Optional<UserLogin> findByLoginNameAndIsActive(String login, boolean isActive) {
        return repo.findByLoginNameAndIsActive(login, isActive);
    }

    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }

    @Override
    public Optional<UserLogin> findByConfirmationToken(String confirmationToken) {
        return repo.findByConfirmationToken(confirmationToken);
    }

    @Override
    public void deleteConfirmationCode(String confirmationCode) {
        repo.deleteConfirmationCode(confirmationCode);
    }
}
