package com.usth.chat_app_api.attachment;

import com.usth.chat_app_api.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Autowired
    private AttachmentRepository repo;

    @Override
    public List<Attachment> findAllByMessage(Page<Message> conversationMessages) {
        return repo.findAllByMessage(conversationMessages);
    }

    @Override
    public Optional<Integer> sumAttachmentByMessageId(Long messageId) {
        return repo.sumAttachmentByMessageId(messageId);
    }
}
