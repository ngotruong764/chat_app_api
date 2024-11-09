package com.usth.chat_app_api.mapper;

import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class MessageMapper {
    public static List<MessageDTO> messageEntityToMessageDTO(List<Message> messageList){
        List<MessageDTO> messageDTOList = new ArrayList<>();
        messageList.forEach(message -> {
            //
            Long userId = message.getCreatorId().getId();
            Long conversationId = message.getConversation().getId();
            String content = message.getContent();
            LocalDateTime messageTime = message.getCreatedAt();
            //
            MessageDTO messageDTO = new MessageDTO(
                    userId,
                    conversationId,
                    content,
                    messageTime
            );
            // add messageDTO to list
            messageDTOList.add(messageDTO);
        });
        return messageDTOList;
    }
}
