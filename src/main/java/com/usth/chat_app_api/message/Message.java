package com.usth.chat_app_api.message;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    private Long id;
    private Long creatorId;
    private String content;
    private LocalDateTime createAt;
    private Message parentMassage;
    public Message(){

    }
    public Message(Long creatorId, String content, LocalDateTime createAt, Message parentMassage) {
        this.creatorId = creatorId;
        this.content = content;
        this.createAt = createAt;
        this.parentMassage = parentMassage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public Message getParentMassage() {
        return parentMassage;
    }

    public void setParentMassage(Message parentMassage) {
        this.parentMassage = parentMassage;
    }
}
