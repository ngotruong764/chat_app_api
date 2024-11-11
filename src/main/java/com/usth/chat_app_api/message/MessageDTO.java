package com.usth.chat_app_api.message;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Long id; // message id
    private Long userId;
    private Long conversationId;
    private String content;
    private LocalDateTime messageTime;
//    private MessageType type;
//
//
//    public enum MessageType {
//        CHAT,
//        JOIN,
//        LEAVE
//    }

    public MessageDTO(Long id,Long userId, Long conversationId, String content, LocalDateTime messageTime) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
    }

    public MessageDTO(Long userId, Long conversationId, String content, LocalDateTime messageTime) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
    }
}
