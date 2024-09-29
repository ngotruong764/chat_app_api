package com.usth.chat_app_api.message_recipient;

import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;

@Entity
@Table(name = "message_recipient")
public class MessageRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    // Liên kết với bảng UserInfo để xác định người nhận tin nhắn
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserInfo recipient;

    // Liên kết với bảng Message để xác định tin nhắn
    @ManyToOne
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    public MessageRecipient() {
    }

    public MessageRecipient(UserInfo recipient, Message message, Boolean isRead) {
        this.recipient = recipient;
        this.message = message;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getRecipient() {
        return recipient;
    }

    public void setRecipient(UserInfo recipient) {
        this.recipient = recipient;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Boolean getIsRead() {
        return isRead;
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
