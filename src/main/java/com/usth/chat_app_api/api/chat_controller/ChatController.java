    package com.usth.chat_app_api.api.chat_controller;

    import com.usth.chat_app_api.message.Message;
    import com.usth.chat_app_api.message.MessageDTO;
    import com.usth.chat_app_api.message.MessageService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.messaging.handler.annotation.MessageMapping;
    import org.springframework.messaging.handler.annotation.Payload;
    import org.springframework.messaging.handler.annotation.SendTo;
    import org.springframework.messaging.simp.SimpMessagingTemplate;
    import org.springframework.stereotype.Controller;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api/chat")
    public class ChatController {
        @Autowired
        private  MessageService messageService;
        @PostMapping("/send")
        public ResponseEntity<String> sendMessage(@RequestBody MessageDTO messageDTO){
            try {
                messageService.sendMessage(messageDTO.getUserId(), messageDTO.getConversationId(), messageDTO.getContent());
                return ResponseEntity.ok("Succesfully");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message");
            }
    }
    }
