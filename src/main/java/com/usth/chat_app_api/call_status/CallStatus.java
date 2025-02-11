package com.usth.chat_app_api.call_status;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Table(name = "call_status")
@Entity
@Getter
@Setter
public class CallStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
}
