package com.usth.chat_app_api.api.user_info;

import com.usth.chat_app_api.core.base.ResponseBase;

public class UserInfoResponse extends ResponseBase {
    private String jwt_token;

    public String getJwt_token() {
        return jwt_token;
    }

    public void setJwt_token(String jwt_token) {
        this.jwt_token = jwt_token;
    }
}
