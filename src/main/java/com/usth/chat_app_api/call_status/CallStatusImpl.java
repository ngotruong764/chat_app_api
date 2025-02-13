package com.usth.chat_app_api.call_status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CallStatusImpl implements ICallStatusService{
    @Autowired
    CallStatusRepository repo;

    @Override
    public Optional<CallStatus> findByCode(String callStatusCode) {
        return repo.findByCode(callStatusCode);
    }
}
