package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConversationParticipantServiceImpl implements ConversationParticipantService {
    @Autowired
    private ConversationParticipantRepository conversationParticipantRepository;
     public ConversationParticipant saveConversationParticipant(ConversationParticipant conversationParticipant){
        return conversationParticipantRepository.save(conversationParticipant);
    }

    @Override
    public void deleteByConversation(Optional<Conversation> conversation) {
        conversationParticipantRepository.deleteByConversation(conversation);
    }
}
