package com.usth.chat_app_api.message_recipient;

import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "message_recipient")
public class MessageRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = true, updatable = false)
    private Long id;

    // Liên kết với bảng UserInfo để xác định người nhận tin nhắn
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = true)
    private UserInfo recipient;

    // Liên kết với bảng Message để xác định tin nhắn
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = true)
    private Message message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;


    public MessageRecipient() {
    }

    public MessageRecipient(UserInfo recipient, Message message, Boolean isRead) {
        this.recipient = recipient;
        this.message = message;
    }

    public MessageRecipient(UserInfo recipient, Message message) {
        this.recipient = recipient;
        this.message = message;
        this.isRead = false;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
