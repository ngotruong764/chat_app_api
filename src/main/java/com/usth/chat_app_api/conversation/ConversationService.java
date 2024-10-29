package com.usth.chat_app_api.conversation;


import java.util.List;

public interface ConversationService {
     List<ConversationDTO> getConversationsWithLastMessage(Long userId);
     Conversation saveConversation(Conversation conversation);
     Conversation createConversation(Long userId, List<Long> participantIds);
     void deleteConversation(Long conversationId);
     Conversation findById(Long conversationId);
     void updateConversationName(Long conversationId,String name);
     void removeUserFromConversation(Long conversationId,Long userIdToRemove);
}
