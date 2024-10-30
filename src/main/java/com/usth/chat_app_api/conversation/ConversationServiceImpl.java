package com.usth.chat_app_api.conversation;

import com.usth.chat_app_api.conversation_participant.ConversationParticipant;
import com.usth.chat_app_api.conversation_participant.ConversationParticipantService;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageService;
import com.usth.chat_app_api.message_recipient.MessageRecipientService;
import com.usth.chat_app_api.user_info.IUserInfoService;
import com.usth.chat_app_api.user_info.UserInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private IUserInfoService userInfoService;
    @Autowired
    private ConversationParticipantService conversationParticipantService;
    @Autowired
    private MessageRecipientService messageRecipientService;
    @Autowired
    private IUserInfoService iUserInfoService;
    @Override
    public List<ConversationDTO> getConversationsWithLastMessage(Long userId) {
        List<Conversation> conversations = conversationRepository.findAllConversationsWithLastMessageByUserId(userId);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();

        for (Conversation conversation : conversations) {
            Message lastMessage = messageService.findFirstByConversationOrderByCreatedAtDesc(conversation)
                    .orElse(null);

            List<ConversationParticipant> participants = conversation.getParticipants();

            List<String> participantNames = participants.stream()
                    .map(ConversationParticipant::getUser)
                    .filter(user -> !user.getId().equals(userId))
                    .map(UserInfo::getFirstName)
                    .collect(Collectors.toList());

            String conversationName;
            if (conversation.getName() != null && !conversation.getName().isEmpty()) {
                conversationName = conversation.getName();
            } else {
                if (participants.size() > 2) {
                    if (participantNames.size() > 2) {
                        conversationName = participantNames.get(0) + ", " + participantNames.get(1) + "...";
                    } else {
                        conversationName = String.join(", ", participantNames);
                    }
                } else {
                    conversationName = participantNames.stream().findFirst().orElse("Unknown User");
                }
            }


            ConversationDTO dto = new ConversationDTO();
            dto.setConversationId(conversation.getId());
            dto.setConversationName(conversationName);
            dto.setLastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet");
            dto.setLastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null);

            conversationDTOs.add(dto);
        }
        conversationDTOs.sort((c1, c2) -> {
            if (c1.getLastMessageTime() == null && c2.getLastMessageTime() == null) {
                return 0;
            } else if (c1.getLastMessageTime() == null) {
                return 1;
            } else if (c2.getLastMessageTime() == null) {
                return -1;
            } else {
                return c2.getLastMessageTime().compareTo(c1.getLastMessageTime());
            }
        });

        return conversationDTOs;
    }



    @Override
    public Conversation saveConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    @Override
    public Conversation createConversation(Long userId, List<Long> participantIds) {

        Conversation newConversation = new Conversation();
        newConversation.setCreator(userInfoService.findUserInforById(userId));
        newConversation.setCreatedAt(LocalDateTime.now());
        newConversation.setActive(true);
        newConversation.setName(null);
        if (participantIds.size() > 1){
            newConversation.setGroup(true);

        } else {
            newConversation.setGroup(false);
        }

        newConversation = conversationRepository.save(newConversation);

        List<ConversationParticipant> conversationParticipants = new ArrayList<>();

        ConversationParticipant creatorParticipant = new ConversationParticipant();
        creatorParticipant.setJoinedAt(LocalDateTime.now());
        creatorParticipant.setUser(userInfoService.findUserInforById(userId));
        creatorParticipant.setConversation(newConversation);
        conversationParticipants.add(creatorParticipant);

        for (Long participantId : participantIds) {
            ConversationParticipant participant = new ConversationParticipant();
            participant.setJoinedAt(LocalDateTime.now());
            participant.setUser(userInfoService.findUserInforById(participantId));
            participant.setConversation(newConversation);
            conversationParticipants.add(participant);
        }

        for (ConversationParticipant participant : conversationParticipants) {
            conversationParticipantService.saveConversationParticipant(participant);
        }

        newConversation.setParticipants(conversationParticipants);


        return newConversation;
    }

    @Override
    @Transactional
    public void deleteConversation(Long conversationId) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversationParticipantService.deleteByConversation(Optional.of(conversation));
            List<Message> messages = messageService.findByConversation(conversation);
            for (Message message : messages) {
                messageRecipientService.deleteMessageRecipientsByMessage(message);
            }
            messageService.deleteByConversation(Optional.of(conversation));
            conversationRepository.deleteById(conversationId);
        } else {
            throw new EntityNotFoundException("Conversation not found with id: " + conversationId);
        }
    }

    @Override
    public Conversation findById(Long conversationId) {
        return conversationRepository.findById(conversationId).orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    @Override
    @Transactional
    public void updateConversationName(Long conversationId, String name) {
        Optional<Conversation> conversationOpt = conversationRepository.findById(conversationId);
        if (conversationOpt.isPresent()) {
            Conversation conversation = conversationOpt.get();
            conversation.setName(name);
            conversationRepository.save(conversation);
        } else {
            throw new EntityNotFoundException("Conversation not found with id: " + conversationId);
        }
    }

    @Override
    @Transactional
    public void removeUserFromConversation(Long conversationId, Long userIdToRemove) {
        List<ConversationParticipant> conversationParticipants = conversationParticipantService.findConversationParticipantByConversationId(conversationId);
        UserInfo userToRemove = iUserInfoService.findUserInforById(userIdToRemove);

        for (ConversationParticipant participant : conversationParticipants) {
            if (participant.getUser().equals(userToRemove)) {
                conversationParticipantService.deleteByUser(userToRemove);
                break;
            }
        }
    }

    @Override
    @Transactional
    public void addUserToConversation(Long conversationId, Long userId) {
       Optional<Conversation> conversation = conversationRepository.findById(conversationId);
       if (conversation.isPresent()){
           ConversationParticipant newConversationParticipant = new ConversationParticipant();
           newConversationParticipant.setConversation(conversation.get());
           newConversationParticipant.setUser(iUserInfoService.findUserInforById(userId));
           newConversationParticipant.setJoinedAt(LocalDateTime.now());
           conversationParticipantService.save(newConversationParticipant);
       }
    }
}