package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<ConversationParticipant> findConversationParticipantByConversationId(Long conversationId) {
        return conversationParticipantRepository.findByConversationId(conversationId);
    }

    @Override
    @Transactional
    public void deleteByUser(UserInfo userInfo) {
        conversationParticipantRepository.deleteByUser(userInfo);
    }

    @Override
    public void save(ConversationParticipant conversationParticipant) {
        conversationParticipantRepository.save(conversationParticipant);
    }

    @Override
    public Optional<ConversationParticipant> findCommonConversationOfTwoPerson(Long currentUserId, Long conversationPartnerId) {
        return conversationParticipantRepository.findCommonConversationOfTwoPerson(currentUserId, conversationPartnerId);
    }
}
