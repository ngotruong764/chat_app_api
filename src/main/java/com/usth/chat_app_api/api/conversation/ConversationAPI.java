package com.usth.chat_app_api.api.conversation;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.conversation.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ConversationAPI {
    @Autowired
    private ConversationService conversationService;
    @GetMapping("/conversation/user")
    public ResponseEntity<List<ConversationDTO>> getConversations(@RequestParam Long userId) {
        List<ConversationDTO> conversations = conversationService.getConversationsWithLastMessage(userId);
        return ResponseEntity.ok(conversations);
    }
    @PostMapping("/createConversation")
    public ResponseEntity<Conversation> createConversation(
            @RequestParam Long userId,
            @RequestParam List<Long> participantIds) {
        try {
            Conversation conversation = conversationService.createConversation(userId, participantIds);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    @DeleteMapping("/deleteConversation")
    public ResponseEntity<?> deleteConversation(@RequestParam Long id) {
        System.out.println("The conversation id deleting is: "+id);
        try {
            conversationService.deleteConversation(id);
            return ResponseEntity.ok("Conversation deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting conversation");
        }
    }
}
