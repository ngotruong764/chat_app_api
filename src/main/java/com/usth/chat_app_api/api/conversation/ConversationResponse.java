package com.usth.chat_app_api.api.conversation;

import com.usth.chat_app_api.conversation.ConversationDTO;
import com.usth.chat_app_api.core.base.ResponseBase;
import com.usth.chat_app_api.message.Message;
import com.usth.chat_app_api.message.MessageDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class ConversationResponse extends ResponseBase {
    private Page<Message> messagePages;
    private List<ConversationDTO> conversationDTOList;
    private List<MessageDTO> messageDTOList;

}
