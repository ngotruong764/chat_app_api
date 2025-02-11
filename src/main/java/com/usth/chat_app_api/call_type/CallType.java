package com.usth.chat_app_api.call_type;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "call_type")
@Entity
@Getter
@Setter
public class CallType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "code")
    private String code;
    @Column(name = "name")
    private String name;
}
