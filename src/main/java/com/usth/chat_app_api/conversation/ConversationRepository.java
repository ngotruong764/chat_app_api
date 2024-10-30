package com.usth.chat_app_api.conversation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("SELECT c FROM Conversation c JOIN FETCH c.messages m WHERE m.id = " +
            "(SELECT MAX(m2.id) FROM Message m2 WHERE m2.conversation.id = c.id) " +
            "AND c.id IN (SELECT cp.conversation.id FROM ConversationParticipant cp WHERE cp.user.id = :userId)")
    List<Conversation> findAllConversationsWithLastMessageByUserId(@Param("userId") Long userId);
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p.user.id = :userId")
    Page<Conversation> findAllByUserId(@Param("userId") Long userId, Pageable pageable);
}
