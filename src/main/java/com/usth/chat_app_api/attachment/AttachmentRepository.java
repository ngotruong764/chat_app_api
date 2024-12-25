package com.usth.chat_app_api.attachment;

import com.usth.chat_app_api.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {

    @Query(value = "select a from Attachment a "
        + "inner join Message b on a.id = b.id")
    List<Attachment> findAllByMessage(Page<Message> conversationMessages);

    @Query(value = "select SUM(a.id) from Attachment a "
        + "where a.message.id = :messageId")
    Optional<Integer> sumAttachmentByMessageId(@Param("messageId") Long messageId);
}
