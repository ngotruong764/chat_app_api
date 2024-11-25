package com.usth.chat_app_api.message;

import com.usth.chat_app_api.attachment.Attachment;
import com.usth.chat_app_api.attachment.AttachmentRepository;
import com.usth.chat_app_api.conversation.Conversation;
import com.usth.chat_app_api.conversation.ConversationRepository;
import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.conversation_participant.ConversationParticipantRepository;
import com.usth.chat_app_api.message_recipient.MessageRecipient;
import com.usth.chat_app_api.message_recipient.MessageRecipientRepository;
import com.usth.chat_app_api.user_info.UserInfo;
import com.usth.chat_app_api.user_info.UserInfoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageRecipientRepository messageRecipientRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private ConversationParticipantRepository conversationParticipantRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;



    @Override
    @Transactional
    public Message sendMessage(Long userId, Long conversationId, String content, LocalDateTime messageTime, List<String> attachments) {
        UserInfo sender = userInfoRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        Message message = new Message();
        message.setCreatorId(sender);
        message.setContent(content);
        message.setCreatedAt(messageTime);
        message.setConversation(conversation);

        Message savedMessage = messageRepository.save(message);

        if (attachments != null && !attachments.isEmpty()) {
            for (String attachmentUrl : attachments) {
                Attachment attachment = new Attachment();
                attachment.setFileUrl(attachmentUrl);
                attachment.setFileType("image");
                attachment.setFileName(attachmentUrl.substring(attachmentUrl.lastIndexOf("/") + 1));
                attachment.setMessage(savedMessage);

                attachmentRepository.save(attachment);
            }
        }
        List<ConversationParticipant> participants = conversationParticipantRepository.findByConversationId(conversationId);
        for (ConversationParticipant participant : participants) {
            if (!participant.getUser().getId().equals(userId)) {
                MessageRecipient messageRecipient = new MessageRecipient(participant.getUser(), savedMessage);
                messageRecipientRepository.save(messageRecipient);
            }
        }

        return savedMessage;
    }


    @Override
    public Optional<Message> findFirstByConversationOrderByCreatedAtDesc(Conversation conversation) {
        return messageRepository.findFirstByConversationOrderByCreatedAtDesc(conversation);
    }

    @Override
    @Transactional
    public void markMessageAsRead(Long messageId, Long recipientId) {
        MessageRecipient messageRecipient = messageRecipientRepository
                .findByMessageIdAndRecipientId(messageId, recipientId)
                .orElseThrow(() -> new RuntimeException("MessageRecipient doesn't exist"));
        messageRecipient.setIsRead(true);
        messageRecipientRepository.save(messageRecipient);
    }

    @Override
    public List<UserInfo> getParticipantsByConversationId(Long conversationId) {
        List<ConversationParticipant> participants = conversationParticipantRepository.findByConversationId(conversationId);
        return participants.stream()
                .map(ConversationParticipant::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByConversation(Conversation conversation) {
        return messageRepository.findByConversation(conversation);
    }

    @Override
    public Page<Message> findByConversation(Long conversationId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return messageRepository.findByConversation(conversationId, pageable);
    }

    @Override
    public void deleteByConversation(Optional<Conversation> conversation) {
        messageRepository.deleteByConversation(conversation);
    }

    @Override
    public List<Object[]> getMessageByConversation(Conversation conversation) {
        List<Message> messages = messageRepository.findAllByConversation(conversation);

        return messages.stream().map(message -> new Object[] {
                message.getCreatorId().getFirstName(),
                message.getCreatedAt(),
                message.getContent()
        }).collect(Collectors.toList());
    }

    @Override
    public List<Object[]> searchMessageByContent(Long conversationId,String keyword) {
        List<Message> messages = messageRepository.findMessageByConversationAndContentContaining(conversationRepository.findById(conversationId), keyword);
        return messages.stream().map(message -> new Object[] {
                message.getCreatorId().getFirstName(),
                message.getCreatedAt(),
                message.getContent()
        }).collect(Collectors.toList());
    }

    @Override
    public Message getMessageDetails(Long conversationId,Long messageId) {
        return messageRepository.findMessageByConversationAndId(conversationRepository.findById(conversationId),messageId);
    }

    public Page<Message> findLatestMessageByConversation(int pageSize, int pageNumber, Long userId) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return messageRepository.findLatestMessageByConversation(userId, pageable);
    }
}
