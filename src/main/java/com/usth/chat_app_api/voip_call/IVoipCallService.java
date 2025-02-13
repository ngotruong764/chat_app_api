package com.usth.chat_app_api.voip_call;

import com.usth.chat_app_api.user_info.UserInfo;

import java.util.Optional;

public interface IVoipCallService {
    VoipCall save(VoipCall voipCall);

    VoipCall findByCaller(UserInfo caller);

    Optional<VoipCall> findById(Long voipCallId);
}
