package com.usth.chat_app_api.message;

import com.usth.chat_app_api.attachment.Attachment;
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
    private String conversationName;
    private String content;
    private LocalDateTime messageTime;
//    private List<String> attachments = List.of();
    private List<Attachment> attachments = List.of();

    public MessageDTO(Long userId, Long conversationId, String content, LocalDateTime messageTime) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
        this.attachments = List.of();
    }

    public MessageDTO(Long userId, Long conversationId, String content, LocalDateTime messageTime, List<Attachment> attachments) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
        this.attachments = (attachments != null) ? attachments : List.of(); // Nếu null, khởi tạo danh sách trống
    }

    public MessageDTO(Long id, Long userId, Long conversationId, String conversationName, String content, LocalDateTime messageTime) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.conversationName = conversationName;
        this.content = content;
        this.messageTime = messageTime;
    }

    public MessageDTO(Long id, Long userId, Long conversationId, String content, LocalDateTime messageTime, List<Attachment> attachments) {
        this.id = id;
        this.userId = userId;
        this.conversationId = conversationId;
        this.content = content;
        this.messageTime = messageTime;
        this.attachments = attachments;
    }
}
