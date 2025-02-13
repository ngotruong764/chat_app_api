package com.usth.chat_app_api.call_status;

import java.util.Optional;

public interface ICallStatusService {
    Optional<CallStatus> findByCode(String callStatusCode);
}
