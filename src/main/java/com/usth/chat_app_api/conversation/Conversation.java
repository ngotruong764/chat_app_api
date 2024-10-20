package com.usth.chat_app_api.conversation;

import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_group")
    private Boolean isGroup;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private UserInfo creator;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "conversation")
    private List<ConversationParticipant> participants;
    @OneToMany(mappedBy = "conversation",cascade = CascadeType.REMOVE)
    private List<Message> messages;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;  // Default to true

    public Conversation() {}

    public Conversation(UserInfo creator, String name, Boolean isGroup, LocalDateTime createdAt, Boolean isActive) {
        this.creator = creator;
        this.name = name;
        this.isGroup = isGroup;
        this.createdAt = createdAt;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public UserInfo getCreator() {
        return creator;
    }

    public void setCreator(UserInfo creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ConversationParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ConversationParticipant> participants) {
        this.participants = participants;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
