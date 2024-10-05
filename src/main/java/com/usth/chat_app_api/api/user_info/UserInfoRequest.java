package com.usth.chat_app_api.api.user_info;


import com.usth.chat_app_api.user_info.UserInfo;

public class UserInfoRequest {
    private UserInfo userInfo;
    private String verificationCode;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserLogin(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }
}
