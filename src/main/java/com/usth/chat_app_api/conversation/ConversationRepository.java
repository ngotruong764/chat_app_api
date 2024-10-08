package com.usth.chat_app_api.conversation;

import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findConversationByCreator(UserInfo creator);
}
