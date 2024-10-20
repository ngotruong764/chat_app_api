package com.usth.chat_app_api.message_recipient;

import com.usth.chat_app_api.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageRecipientServiceImpl implements MessageRecipientService {
    @Autowired
    private MessageRecipientRepository messageRecipientRepository;

    @Override
    public void deleteMessageRecipientsByMessage(Message message) {
        messageRecipientRepository.deleteMessageRecipientsByMessage(message);
    }
}
