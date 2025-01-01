package com.usth.chat_app_api.api.conversation;

import com.usth.chat_app_api.attachment.Attachment;
import com.usth.chat_app_api.attachment.AttachmentService;
import com.usth.chat_app_api.aws.IAwsS3Service;
import com.usth.chat_app_api.constant.ApplicationConstant;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.conversation.ConversationService;
import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.conversation_participant.ConversationParticipantService;
import com.usth.chat_app_api.utils.Helper;
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

import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private IAwsS3Service awsS3Service;
    @Autowired
    private ConversationParticipantService conversationParticipantService;

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
//            // get 20 latest messages of an user
//            Page<Message> latestMessagesPage = messageService.findLatestMessageByConversation(pageSize, pageNumber, userId);
//            // get latest message list
//            List<Message> latestMessageList = latestMessagesPage.getContent();
//            // convert list message to list dto
//            List<ConversationDTO> conversationDTOList = ConversationMapper.messageToConversationDTO(latestMessageList);
            List<ConversationDTO> conversations = conversationService.getConversationsWithLastMessage(userId, pageNumber, pageSize);

            response.setConversationDTOList(conversations);
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
     * fetch 50 messages of a conversation
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

            // Find list of message
            Page<Message> conversationMessages = messageService.findByConversation(conversationId, pageNumber, pageSize);

            // find attachment by message_id
            Map<Long, List<Attachment>> messageAttachmentMap = attachmentService.findAllByMessage(conversationMessages)
                    .stream().collect(Collectors.groupingBy(attachment -> attachment.getMessage().getId()));

            // set attachment to message
            for (Message message : conversationMessages){
                Long messageId = message.getId();
                List<Attachment> attachmentList = messageAttachmentMap.getOrDefault(messageId, new ArrayList<>());

                if(!attachmentList.isEmpty()){
                    for(Attachment attachment : attachmentList){
                        // get attachment content from AWS S3 by bucket and key
                        byte[] attachmentContent = awsS3Service.downLoadObject(ApplicationConstant.AWS_BUCKET_NAME, attachment.getFileUrl());

                        // convert byte to base64 encoded
                        if(attachmentContent != null && attachmentContent.length > 0){
                            // set base64 encoded content to Attachment
                            String base64Data = Base64.getEncoder().encodeToString(attachmentContent);
                            attachment.setAttachmentContent(base64Data);
                        }
                    }
                }
                // set attachment list to Message
                message.setAttachments(attachmentList);
            }

            // map message to message DTO
            List<MessageDTO> messageDTOList = MessageMapper
                    .messageEntityToMessageDTO(conversationMessages.getContent());

            // sort message DTO list by messageTime in asc order
            messageDTOList = messageDTOList.stream()
                    .sorted(Comparator.comparing(MessageDTO::getMessageTime))
                    .collect(Collectors.toList());

            // set response
            // if is last page --> set to response
            if (conversationMessages.isLast()){
                response.setLastPage(true);
            } else {
                response.setLastPage(false);
            }
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

    /*
    * Method to find Conversation
    *   if it does not have conversation --> create new one
    *   else return the existed conversation
    */

    @PostMapping(value = "/fetchConversationOrCreate")
    public ResponseEntity<ConversationResponse> fetchConversationOrCreate(@RequestBody ConversationRequest request) {
        ConversationResponse response = new ConversationResponse();
        UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isGroup = false;
        try{
            Long userId = userInfo.getId();

            // get params
            Long conversationPartnerId = request.conversationPartnerId;


            // find Conversation
            Optional<ConversationParticipant> conversationParticipant = conversationParticipantService
                    .findCommonConversationOfTwoPerson(userId, conversationPartnerId);

            if(conversationParticipant.isPresent()){
                ConversationDTO conversationDTO = convertConversationToConversationDTOWithLastMessage(conversationParticipant.get().getConversation(), userId);
                response.setConversationDTO(conversationDTO);
            } else {
                // create new conversation if the conversation between 2 person does not exist
                Conversation conversation = conversationService.createConversation(userId, List.of(conversationPartnerId));
                ConversationDTO conversationDTO = convertConversationToConversationDTOWithLastMessage(conversation, userId);
                response.setConversationDTO(conversationDTO);
            }

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


    public ConversationDTO convertConversationToConversationDTOWithLastMessage(Conversation conversation, Long currentUserId){
        final String bucketName = ApplicationConstant.AWS_BUCKET_NAME;

        // find last message of a conversation
        Optional<Message> lastMessageOptional = messageService.findFirstByConversationOrderByCreatedAtDesc(conversation);
        Message lastMessage = lastMessageOptional.orElse(null);

        String lastMessageContent = "Starting conversation";
        lastMessageContent = lastMessage != null ? lastMessage.getContent() : lastMessageContent; // set last message

        // find number of Attachment in a Message by MessageId
        int attachmentCount = 0;
        if(lastMessage != null){
            attachmentCount = attachmentService.sumAttachmentByMessageId(lastMessage.getId()).orElse(0);

        }
        if(attachmentCount == 1){
            lastMessageContent = "Send an attachment";
        } else if (attachmentCount > 1){
            lastMessageContent = "Send attachments";
        }

        // if conversation is not group --> we get image of another person
        String avatarBase64Encoded = "";
        if(!conversation.getGroup()){
            Optional<UserInfo> anotherUser = conversation.getParticipants().stream()
                    .map(ConversationParticipant::getUser)
                    .filter(user -> !user.getId().equals(currentUserId)).findAny();
            // if it has person
            if(anotherUser.isPresent()){
                // get user avatar path url
                String userAvatarKey = anotherUser.get().getProfilePicture();
                if(userAvatarKey != null && !userAvatarKey.isEmpty()){
                    // download user avatar
                    byte[] bytes = awsS3Service.downLoadObject(bucketName, userAvatarKey);
                    // convert bytes to base64
                    if(bytes.length > 0 && Helper.isValidImg(bytes)){
                        anotherUser.get().setProfilePicture(null);
                        // convert byte[] to base64
                        avatarBase64Encoded = Base64.getEncoder().encodeToString(bytes);
                    }
                }
            }
        }

        // find the user has the last message
        Long lastMessageUserId = 0L;
        String lastMessageUserName = "";
        if(lastMessage != null){
            UserInfo lastMessageUser = lastMessage.getCreatorId();
            lastMessageUserId = lastMessageUser.getId();
            lastMessageUserName = lastMessageUser.getUsername();
        }

        List<String> participantNames = conversation.getParticipants().stream()
                .map(ConversationParticipant::getUser)
                .filter(user -> !user.getId().equals(currentUserId))
                .map(UserInfo::getUsername)
                .collect(Collectors.toList());

        // set conversation name
        String conversationName;
        // if conversation has a name
        if (conversation.getName() != null && !conversation.getName().isEmpty()) {
            conversationName = conversation.getName();
        } else {
            if (participantNames.size() > 2) {
                // if conversation is a group and not has a name
                conversationName = participantNames.get(0) + ", " + participantNames.get(1) + "...";
            } else {
                conversationName = String.join(", ", participantNames);
            }
        }

        ConversationDTO dto = new ConversationDTO();
        dto.setConversationId(conversation.getId());
        dto.setConversationName(conversationName);
        dto.setLastMessage(lastMessageContent);
        dto.setLastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null);
        dto.setConversationCreatedAt(conversation.getCreatedAt());
        dto.setUserLastMessageId(lastMessageUserId);
        dto.setUserLastMessageName(lastMessageUserName);
        if(!avatarBase64Encoded.isEmpty()){
            dto.setConservationAvatar(avatarBase64Encoded);
        }
        return dto;
    }

}
