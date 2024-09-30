package com.usth.chat_app_api.api.user_login;

import com.usth.chat_app_api.user_login.UserLogin;

public class UserLoginRequest {
    private UserLogin userLogin;
    private String confirmationCode;

    public UserLogin getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(UserLogin userLogin) {
        this.userLogin = userLogin;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }
}
