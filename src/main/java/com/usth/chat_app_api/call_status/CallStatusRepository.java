package com.usth.chat_app_api.call_status;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CallStatusRepository extends JpaRepository<CallStatus, Long> {
    Optional<CallStatus> findByCode(String callStatusCode);
}
