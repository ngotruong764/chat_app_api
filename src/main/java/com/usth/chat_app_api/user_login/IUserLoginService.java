package com.usth.chat_app_api.user_login;

import java.util.Optional;

public interface IUserLoginService {
    Optional<UserLogin> findByEmail(String email);
    UserLogin saveUserLogin(UserLogin userLogin);
    Optional<UserLogin> findByLoginName(String loginName);
    Optional<UserLogin> findByEmailAndIsActive(String email, boolean isActive);
    Optional<UserLogin> findByLoginNameAndIsActive(String login, boolean isActive);
    void deleteByEmail(String email);
    Optional<UserLogin> findByConfirmationToken(String confirmationToken);

    void deleteConfirmationCode(String confirmationCode);
}
