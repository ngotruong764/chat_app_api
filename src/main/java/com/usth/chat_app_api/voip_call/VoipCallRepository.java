package com.usth.chat_app_api.voip_call;

import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoipCallRepository extends JpaRepository<VoipCall, Long> {
    VoipCall findByCaller(UserInfo caller);
}
