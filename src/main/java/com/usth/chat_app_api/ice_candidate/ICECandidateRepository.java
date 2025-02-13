package com.usth.chat_app_api.ice_candidate;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.voip_call.VoipCall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICECandidateRepository extends JpaRepository<ICECandidate, Long> {

    @Query(value = "select a from ICECandidate a "
            + "where a.voipCallId.id = :voipCallId "
    )
    List<ICECandidate> findAllByVoipCallId(@Param("voipCallId") Long voipCallId);
}
