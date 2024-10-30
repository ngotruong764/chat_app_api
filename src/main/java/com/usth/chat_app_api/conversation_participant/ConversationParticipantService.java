package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;

import java.util.List;
import java.util.Optional;

public interface ConversationParticipantService {
   public ConversationParticipant saveConversationParticipant(ConversationParticipant conversationParticipant);
   void deleteByConversation(Optional<Conversation> conversation);
   List<ConversationParticipant> findConversationParticipantByConversationId(Long conversationId);
   void deleteByUser(UserInfo userInfo);
   void save(ConversationParticipant conversationParticipant);
}
