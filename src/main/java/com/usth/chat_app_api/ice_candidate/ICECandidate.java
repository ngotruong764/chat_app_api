package com.usth.chat_app_api.ice_candidate;

import com.usth.chat_app_api.voip_call.VoipCall;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Table(name = "ice_candidate")
@Entity
@Getter
@Setter
public class ICECandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ice_content")
    private String iceContent;

    @JoinColumn(name = "voip_call_id", referencedColumnName = "id")
    @ManyToOne
    private VoipCall voipCallId;
}
