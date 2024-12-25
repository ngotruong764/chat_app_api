package com.usth.chat_app_api.mapper;

import com.usth.chat_app_api.attachment.Attachment;
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
            Long messageId = message.getId();
            Long userId = message.getCreatorId().getId();
            Long conversationId = message.getConversation().getId();
            String content = message.getContent();
            LocalDateTime messageTime = message.getCreatedAt();
            List<Attachment> attachmentList = message.getAttachments();
            //
            MessageDTO messageDTO = new MessageDTO(
                    messageId,
                    userId,
                    conversationId,
                    content,
                    messageTime,
                    attachmentList
            );
            // add messageDTO to list
            messageDTOList.add(messageDTO);
        });
        return messageDTOList;
    }
}
