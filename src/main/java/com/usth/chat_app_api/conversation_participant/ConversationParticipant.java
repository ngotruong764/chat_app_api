package com.usth.chat_app_api.conversation_participant;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participant")
public class ConversationParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserInfo user;
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    public ConversationParticipant() {}

    public ConversationParticipant(Conversation conversation, UserInfo user, LocalDateTime joinedAt) {
        this.conversation = conversation;
        this.user = user;
        this.joinedAt = joinedAt;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
