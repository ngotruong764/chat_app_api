package com.usth.chat_app_api.message;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageService {

    Message sendMessage(Long userId, Long conversationId, String content, LocalDateTime messageTime);

    Optional<Message> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation);

    void markMessageAsRead(Long messageId, Long recipientId);

    List<UserInfo> getParticipantsByConversationId(Long conversationId);
    List<Message> findByConversation(Conversation conversation);
    Page<Message> findByConversation(Long conversationId, int pageNumber, int pageSize);
    void deleteByConversation(Optional<Conversation> conversation);
    List<Object[]> getMessageByConversation(Conversation conversation);
    List<Object[]> searchMessageByContent(Long conversationId,String keyword);
    Message getMessageDetails(Long conversationId,Long messageId);

    Page<Message> findLatestMessageByConversation(int pageSize, int pageNumber, Long userId);
}
