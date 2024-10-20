package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;

import java.util.Optional;

public interface ConversationParticipantService {
   public ConversationParticipant saveConversationParticipant(ConversationParticipant conversationParticipant);
   void deleteByConversation(Optional<Conversation> conversation);
}
