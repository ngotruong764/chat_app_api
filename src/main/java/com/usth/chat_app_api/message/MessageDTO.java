package com.usth.chat_app_api.message;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Long userId;
    private Long conversationId;
    private String content;
//    private MessageType type;
//
//
//    public enum MessageType {
//        CHAT,
//        JOIN,
//        LEAVE
//    }

    public MessageDTO(Long userId, Long conversationId, String content) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
    }
}
