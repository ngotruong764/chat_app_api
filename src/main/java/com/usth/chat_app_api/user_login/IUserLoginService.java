package com.usth.chat_app_api.user_login;

import java.util.Optional;

public interface IUserLoginService {
    Optional<UserLogin> findByEmail(String email);
    Optional<UserLogin> findByEmailAndIsActive(String email, boolean isActive);
    void deleteByEmail(String email);
    Optional<UserLogin> findByConfirmationToken(String confirmationToken);
}
