package com.usth.chat_app_api.call_status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CallStatusRepository extends JpaRepository<CallStatus, Long> {
}
