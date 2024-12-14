package com.usth.chat_app_api.api.message;

import com.usth.chat_app_api.aws.IAwsS3Service;
import com.usth.chat_app_api.aws.IAwsSNSService;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/message")
public class MessageApi {
    @Autowired
    private MessageService messageService;
    @Autowired
    private ConversationService conversationService;
    @Autowired
    private IAwsS3Service awsService;
    @Autowired
    private IAwsSNSService awsSNSService;
    @GetMapping("/conversation")
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

//    @PostMapping("/test_put_s3")
//    public void testPutS3(@RequestBody UserInfoRequest request) throws Exception {
//        System.out.println("In test");
//        String base64 = request.base64;
//        String bucketName = "first-s3-bucket-nqt";
//        String key = "ba12-18/img7.png";
//        Long contentLength = 1L;
//        String contentType = "";
//        boolean isUploaded = awsService.uploadObject(bucketName, key, contentLength, contentType, base64);
//        System.out.println(isUploaded);
//    }

    @PostMapping("/test_put_s3")
    public void testPutS3() throws Exception {
        System.out.println("In test");
        awsSNSService.publishNotification("dn8GP5gxS3OTGbCEoV3m4S:APA91bHA4s1ZBQ_0vGYktnY6PcqU1lxsG3iU14bG-aLfm43IuidXhrL8L71uTDd5X1DwwHo02rkC6js7dZHuppw3fwmrH5oGQkobGkh2JWuhAhJIbjcEsJU", "title", "body");
    }
}
