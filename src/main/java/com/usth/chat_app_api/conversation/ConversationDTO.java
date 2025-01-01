package com.usth.chat_app_api.conversation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class ConversationDTO {
    private Long conversationId;
    private String conversationName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime conversationCreatedAt;
    private Long userLastMessageId;
    private String userLastMessageName;
    private String conservationAvatar;

    public ConversationDTO(Long conversationId, String conversationName, String lastMessage, LocalDateTime lastMessageTime) {
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }
}
