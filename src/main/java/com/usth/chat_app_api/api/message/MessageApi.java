package com.usth.chat_app_api.api.message;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class MessageApi {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;
    @GetMapping("/messages/conversation")
    public ResponseEntity<List<Object[]>> getAllMessage(@RequestParam Long conversationId) {
        Conversation conversation = conversationService.findById(conversationId);
        List<Object[]> messages = messageService.getMessageByConversation(conversation);
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/searchMessagesByKeyword")
    public ResponseEntity<List<Object[]>> searchMessages(
            @RequestParam Long conversationId,
            @RequestParam String keyword) {
        List<Object[]> messages = messageService.searchMessageByContent(conversationId, keyword);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/get_message_detail")
    public ResponseEntity<Map<String,Object>> getMessageDetail(@RequestParam Long conversationId,Long messageId){
        Message message = messageService.getMessageDetails(conversationId,messageId);
        Map<String, Object> response = new HashMap<>();
        response.put("creatorName", message.getCreatorId().getFirstName());
        response.put("createdAt", message.getCreatedAt());
        return ResponseEntity.ok(response);
    }
}
