package com.usth.chat_app_api.message;

import com.usth.chat_app_api.conversation.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);
}
