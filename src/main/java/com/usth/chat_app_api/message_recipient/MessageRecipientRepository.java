package com.usth.chat_app_api.message_recipient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRecipientRepository extends JpaRepository<MessageRecipient,Long> {
    Optional<MessageRecipient> findByMessageIdAndRecipientId(Long messageId, Long recipientId);
}
