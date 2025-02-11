package com.usth.chat_app_api.call_status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CallStatusImpl implements ICallStatusService{
    @Autowired
    CallStatusRepository repo;
}
