package com.usth.chat_app_api.api.conversation;
import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class Conversation {
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private IUserInfoService userInfoService;
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ConversationDTO>> getConversations(@PathVariable Long userId) {
        List<ConversationDTO> conversations = conversationService.getConversationsWithLastMessage(userId);
        return ResponseEntity.ok(conversations);
    }
}
