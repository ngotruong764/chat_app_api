package com.usth.chat_app_api.ice_candidate;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.voip_call.VoipCall;

import java.util.List;

public interface IICECandidateService {
    ICECandidate save(ICECandidate iceCandidate);

    List<ICECandidate> findAllByVoipCallId(Long voipCallId);
}
