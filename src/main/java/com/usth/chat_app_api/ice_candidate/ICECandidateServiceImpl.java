package com.usth.chat_app_api.ice_candidate;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.voip_call.VoipCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ICECandidateServiceImpl implements IICECandidateService{
    @Autowired
    ICECandidateRepository repo;

    @Override
    public ICECandidate save(ICECandidate iceCandidate) {
        return repo.save(iceCandidate);
    }

    @Override
    public List<ICECandidate> findAllByVoipCallId(Long voipCallId) {
        return repo.findAllByVoipCallId(voipCallId);
    }
}
