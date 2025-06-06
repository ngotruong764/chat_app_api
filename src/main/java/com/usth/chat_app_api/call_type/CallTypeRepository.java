package com.usth.chat_app_api.call_type;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallTypeRepository extends JpaRepository<CallType, Long> {
    CallType findByCode(String callType);
}
