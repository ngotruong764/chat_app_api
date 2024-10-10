package com.usth.chat_app_api.conversation;

import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageRepository;
import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Override
    public List<ConversationDTO> getConversationsWithLastMessage(Long userId) {
        List<Conversation> conversations = conversationRepository.findAllConversationsWithLastMessageByUserId(userId);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();

        for (Conversation conversation : conversations) {
            Message lastMessage = messageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation)
                    .orElse(null);
            List<ConversationParticipant> participants = conversation.getParticipants();
            String conversationName;
            if (participants.size() > 2) {
                conversationName = conversation.getName();
            } else {
                conversationName = participants.stream()
                        .map(ConversationParticipant::getUser)
                        .filter(user -> !user.getId().equals(userId))
                        .findFirst()
                        .map(UserInfo::getFirstName)
                        .orElse("Unknown User");
            }
            ConversationDTO dto = new ConversationDTO();
            dto.setConversationId(conversation.getId());
            dto.setConversationName(conversationName);
            dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
            dto.setLastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null);

            conversationDTOs.add(dto);
        }
        return conversationDTOs;
    }

}