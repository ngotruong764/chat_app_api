package com.usth.chat_app_api.friend;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FriendServiceImpl implements IFriendService {
    @Autowired
    private FriendRepository repo;
}
