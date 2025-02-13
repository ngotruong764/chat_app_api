package com.usth.chat_app_api.voip_call;

import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoipCallServiceImpl implements IVoipCallService{
    @Autowired
    VoipCallRepository repo;

    @Override
    public VoipCall save(VoipCall voipCall) {
        return repo.save(voipCall);
    }

    @Override
    public VoipCall findByCaller(UserInfo caller) {
        return repo.findByCaller(caller);
    }

    @Override
    public Optional<VoipCall> findById(Long voipCallId) {
        return repo.findById(voipCallId);
    }
}
