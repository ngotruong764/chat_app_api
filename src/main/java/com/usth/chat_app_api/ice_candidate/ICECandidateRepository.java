package com.usth.chat_app_api.ice_candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICECandidateRepository extends JpaRepository<ICECandidate, Long> {
}
