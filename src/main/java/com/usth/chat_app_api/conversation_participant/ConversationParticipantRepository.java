package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByConversationId(Long conversationId);
    void deleteByConversation(Optional<Conversation> conversation);
    void deleteByUser(UserInfo userInfo);

    @Query(value = "select a from ConversationParticipant a " +
            "inner join ConversationParticipant b on a.conversation.id = b.conversation.id " +
            "where (a.user.id = :currentUserId and b.user.id = :conversationPartnerId) ")
    Optional<ConversationParticipant> findCommonConversationOfTwoPerson(
            @Param("currentUserId") Long currentUserId,
            @Param("conversationPartnerId") Long conversationPartnerId);
}
