package com.usth.chat_app_api.ice_candidate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ICECandidateServiceImpl implements IICECandidateService{
    @Autowired
    ICECandidateRepository repo;
}
