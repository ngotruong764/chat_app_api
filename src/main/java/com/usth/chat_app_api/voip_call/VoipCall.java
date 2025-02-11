package com.usth.chat_app_api.voip_call;

import com.usth.chat_app_api.call_status.CallStatus;
import com.usth.chat_app_api.call_type.CallType;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Table(name = "voip_call")
@Entity
@Getter
@Setter
public class VoipCall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "caller", referencedColumnName = "id")
    @ManyToOne
    private UserInfo caller;

    @Column(name = "time_start")
    private Timestamp timeStart;

    @Column(name = "time_end")
    private Timestamp timeEnd;

    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    @ManyToOne
    private Conversation conversationId;

    @ManyToOne
    @JoinColumn(name = "call_type_id", referencedColumnName = "id")
    private CallType callTypeId;

    @JoinColumn(name = "call_status_id", referencedColumnName = "id")
    @ManyToOne
    private CallStatus callStatusId;

}
