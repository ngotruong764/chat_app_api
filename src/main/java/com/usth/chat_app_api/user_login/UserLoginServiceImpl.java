package com.usth.chat_app_api.user_login;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserLoginServiceImpl implements IUserLoginService{
    private UserLoginRepository repo;

    @Override
    public Optional<UserLogin> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @Override
    public Optional<UserLogin> findByEmailAndIsActive(String email, boolean isActive) {
        return repo.findByEmailAndIsActive(email, isActive);
    }

    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }

    @Override
    public Optional<UserLogin> findByConfirmationToken(String confirmationToken) {
        return repo.findByConfirmationToken(confirmationToken);
    }
}
