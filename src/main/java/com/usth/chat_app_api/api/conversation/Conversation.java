package com.usth.chat_app_api.api.conversation;
import com.usth.chat_app_api.conversation.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
;
import java.util.List;

@RestController
@RequestMapping("/")
public class Conversation {
    @Autowired
    private ConversationService conversationService;
}
