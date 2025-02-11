package com.usth.chat_app_api.voip_call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VoipCallServiceImpl implements IVoipCallService{
    @Autowired
    VoipCallRepository repo;
}
