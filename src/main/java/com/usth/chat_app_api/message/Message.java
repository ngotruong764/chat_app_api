package com.usth.chat_app_api.message;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.message_recipient.MessageRecipient;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false,updatable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private UserInfo creatorId;
    @Column(name = "content",nullable = false)
    private String content;
    @Column(name = "created_at",nullable = false)
    private LocalDateTime createdAt;
    @ManyToOne
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;
    @OneToMany(mappedBy = "message")
    private List<MessageRecipient> recipients;
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    public Message() {
    }

    public Message(UserInfo creatorId, String content, LocalDateTime createdAt, Message parentMessage, Conversation conversation) {
        this.creatorId = creatorId;
        this.content = content;
        this.createdAt = createdAt;
        this.parentMessage = parentMessage;
        this.conversation = conversation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserInfo getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(UserInfo creator) {
        this.creatorId = creator;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Message getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(Message parentMessage) {
        this.parentMessage = parentMessage;
    }

    public List<MessageRecipient> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<MessageRecipient> recipients) {
        this.recipients = recipients;
    }
}
