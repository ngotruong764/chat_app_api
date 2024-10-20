package com.usth.chat_app_api.message_recipient;

import com.usth.chat_app_api.message.Message;

public interface MessageRecipientService {
    void deleteMessageRecipientsByMessage(Message message);
}
