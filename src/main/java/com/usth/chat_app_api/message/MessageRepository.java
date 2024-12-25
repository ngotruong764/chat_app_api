package com.usth.chat_app_api.message;

import com.usth.chat_app_api.conversation.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);
    void deleteByConversation(Optional<Conversation> conversation);
    List<Message> findByConversation(Conversation conversation);
    List<Message> findAllByConversation(Conversation conversation);
    List<Message> findMessageByConversationAndContentContaining(Optional<Conversation> conversation, String keyword);
    Message findMessageByConversationAndId(Optional<Conversation> conversation, Long messageId);

    @Query(value = "select a from Message a "
        + "where a.conversation.id = :conversationId "
        + "order by a.createdAt desc ")
    Page<Message> findByConversation(
            @Param("conversationId") Long conversationI, Pageable pageable);

    @Query(value = "select a from Message a "
        + "right join ConversationParticipant b on a.conversation.id = b.conversation.id "
        + "where b.user.id = :userId "
        + "group by a.conversation.id "
        + "order by a.createdAt desc ")
    Page<Message> findLatestMessageByConversation(
            @Param("userId") Long userId, Pageable pageable);
}
