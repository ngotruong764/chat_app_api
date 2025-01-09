package com.usth.chat_app_api.api.user_info;


import com.usth.chat_app_api.user_info.UserInfo;

public class UserInfoRequest {
    private UserInfo userInfo;
    private String verificationCode;
    private String userEmail;

    public String base64;

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

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
