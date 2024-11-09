package com.usth.chat_app_api.api.conversation;

import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.conversation.ConversationService;
import jakarta.persistence.EntityNotFoundException;
import com.usth.chat_app_api.core.base.ResponseMessage;
import com.usth.chat_app_api.mapper.ConversationMapper;
import com.usth.chat_app_api.mapper.MessageMapper;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageDTO;
import com.usth.chat_app_api.message.MessageService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/conservation")
@Slf4j
public class ConversationAPI {
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private MessageService messageService;
    @GetMapping("/getConversation/user")
    public ResponseEntity<?> getConversations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ConversationDTO> conversations = conversationService.getConversationsWithLastMessage(userId, page, size);

        if (conversations.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No conversation");
            return ResponseEntity.ok(response);
        }
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
        System.out.println("The conversation id deleting is: " + id);
        try {
            conversationService.deleteConversation(id);
            return ResponseEntity.ok("Conversation deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting conversation");
        }
    }
    @PutMapping("/updateConversation")
    public ResponseEntity<?> updateConversation(
            @RequestParam Long conversationId,
            @RequestParam(required = false) String newName,
            @RequestParam(required = false) String newImage) {
        try {
            if (newName != null) {
                conversationService.updateConversationName(conversationId, newName);
            }
            if (newImage != null) {
//                conversationService.updateConversationImage(conversationId, newImage);
            }
            return ResponseEntity.ok("Conversation updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating conversation");
        }
    }
    @DeleteMapping("/deleteUserFromConversation")
    public ResponseEntity<String> removeUserFromConversation(
            @RequestParam Long conversationId,
            @RequestParam Long userId) {
        try {
            conversationService.removeUserFromConversation(conversationId, userId);
            return ResponseEntity.ok("User removed from conversation successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred while removing the user from the conversation");
        }
    }
    @PostMapping("/addUserToConversation")
    public ResponseEntity<String> addUserToConversation(@RequestParam Long conversationId,@RequestParam Long userId) {
        try {
            conversationService.addUserToConversation(conversationId, userId);
            return ResponseEntity.ok("User added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding user to conversation");
        }
    }

    /**
     * Get 20 conversations with the last message
     */
    @PostMapping(value = "/getListConversation")
    public ResponseEntity<ConversationResponse> fetchConversation(@RequestBody ConversationRequest request) {
        ConversationResponse response = new ConversationResponse();
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try{
            Long userId = userInfo.getId();
            // get params
            int pageSize = request.pageSize;
            int pageNumber = request.pageNumber;
            // get 20 latest messages of an user
            Page<Message> latestMessagesPage = messageService.findLatestMessageByConversation(pageSize, pageNumber, userId);
            // get latest message list
            List<Message> latestMessageList = latestMessagesPage.getContent();
            // convert list message to list dto
            List<ConversationDTO> conversationDTOList = ConversationMapper.messageToConversationDTO(latestMessageList);
            response.setConversationDTOList(conversationDTOList);
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e){
            log.info(e.toString());
            response.setMessage(e.getMessage());
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * fetch 40 messages of a conversation
     */
    @PostMapping(value = "/fetchConversationMessage")
    public ResponseEntity<ConversationResponse> fetchConversationMessage(@RequestBody ConversationRequest request) {
        ConversationResponse response = new ConversationResponse();
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            Long userId = userInfo.getId();
            // get params
            int pageSize = request.pageSize; // 40 messages
            int pageNumber = request.pageNumber;
            Long conversationId = request.conversationId;
            Page<Message> conversationMessages = messageService.findByConversation(conversationId, pageNumber, pageSize);
            // map message to message DTO
            List<MessageDTO> messageDTOList = MessageMapper.messageEntityToMessageDTO(conversationMessages.getContent());
            // set response
            response.setMessageDTOList(messageDTOList);
            response.setMessage(ResponseMessage.getMessage(HttpStatus.OK.value()));
            response.setResponseCode(HttpStatus.OK.value());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            log.info(e.toString());
            response.setMessage(e.getMessage());
            response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
