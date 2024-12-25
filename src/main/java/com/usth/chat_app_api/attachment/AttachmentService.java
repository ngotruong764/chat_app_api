package com.usth.chat_app_api.attachment;

import com.usth.chat_app_api.message.Message;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttachmentService {
    List<Attachment> findAllByMessage(Page<Message> conversationMessages);

    Optional<Integer> sumAttachmentByMessageId(Long messageId);
}
