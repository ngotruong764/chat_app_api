package com.usth.chat_app_api.conversation;

import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public interface ConversationService {
    public List<ConversationDTO> getConversationsWithLastMessage(Long userId);
}
