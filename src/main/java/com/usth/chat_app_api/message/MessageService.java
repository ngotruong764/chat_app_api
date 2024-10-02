package com.usth.chat_app_api.message;

import com.usth.chat_app_api.user_info.UserInfo;
import java.util.List;

public interface MessageService {
    
    Message sendMessage(Long userId, Long conversationId, String content);


    void markMessageAsRead(Long messageId, Long recipientId);


    List<UserInfo> getParticipantsByConversationId(Long conversationId);
}
