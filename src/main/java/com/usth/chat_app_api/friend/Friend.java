package com.usth.chat_app_api.friend;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Table(name = "friend")
@Entity
@Getter
@Setter
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "uid1")
    private Long uid1;
    @Column(name = "uid2")
    private Long uid2;
    @Column(name = "createAt")
    private Timestamp createAt;
    @Column(name = "updateAt")
    private Timestamp updateAt;
    @Column(name = "status")
    private FriendStatus status;
}
