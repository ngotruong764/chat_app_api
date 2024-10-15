package com.usth.chat_app_api.api.user_info;

import com.usth.chat_app_api.core.base.ResponseBase;
import com.usth.chat_app_api.user_info.UserInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse extends ResponseBase {
    private String jwt_token;
    private UserInfo userInfo;
}
