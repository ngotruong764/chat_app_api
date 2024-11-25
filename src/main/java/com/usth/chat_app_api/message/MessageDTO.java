package com.usth.chat_app_api.message;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long userId;
    private Long conversationId;
    private String content;
    private LocalDateTime messageTime;
    private List<String> attachments = List.of();

    public MessageDTO(Long userId, Long conversationId, String content, LocalDateTime messageTime) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
        this.attachments = List.of();
    }

    public MessageDTO(Long userId, Long conversationId, String content, LocalDateTime messageTime, List<String> attachments) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
        this.attachments = (attachments != null) ? attachments : List.of(); // Nếu null, khởi tạo danh sách trống
    }
}
