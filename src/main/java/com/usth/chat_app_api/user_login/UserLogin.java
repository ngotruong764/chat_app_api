package com.usth.chat_app_api.user_login;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Table(name = "user_login")
@Entity
public class UserLogin {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "login_name")
    private String loginName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "confirmation_token")
    private String confirmationToken;
    @Column(name = "token_create_at")
    private Timestamp tokenCreateAt;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "refresh_token_at")
    private Timestamp refreshTokenAt;
    @Column(name = "is_active")
    private Boolean isActive;
    @Column(name = "is_reported")
    private Boolean is_reported;
    @Column(name = "is_blocked")
    private Boolean is_blocked;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public Timestamp getTokenCreateAt() {
        return tokenCreateAt;
    }

    public void setTokenCreateAt(Timestamp tokenCreateAt) {
        this.tokenCreateAt = tokenCreateAt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Timestamp getRefreshTokenAt() {
        return refreshTokenAt;
    }

    public void setRefreshTokenAt(Timestamp refreshTokenAt) {
        this.refreshTokenAt = refreshTokenAt;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIs_reported() {
        return is_reported;
    }

    public void setIs_reported(Boolean is_reported) {
        this.is_reported = is_reported;
    }

    public Boolean getIs_blocked() {
        return is_blocked;
    }

    public void setIs_blocked(Boolean is_blocked) {
        this.is_blocked = is_blocked;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
