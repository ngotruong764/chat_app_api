package com.usth.chat_app_api.mapper;

import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.message.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class ConversationMapper {
    public static List<ConversationDTO> messageToConversationDTO(List<Message> messageList){
        List<ConversationDTO> conversationDTOList = new ArrayList<>();
        messageList.forEach(message -> {
            Long conversationId = message.getConversation().getId();
            String conversationName = message.getConversation().getName();
            String lastMessage = message.getContent();
            LocalDateTime lastMessageTime = message.getCreatedAt();
            ConversationDTO conversationDTO = new ConversationDTO(
                                                    conversationId,
                                                    conversationName,
                                                    lastMessage,
                                                    lastMessageTime);
            // add to list
            conversationDTOList.add(conversationDTO);
        });
        return conversationDTOList;
    }
}
