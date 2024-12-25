package com.usth.chat_app_api.attachment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.usth.chat_app_api.message.Message;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "attachment")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type", nullable = false)
    private String fileType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @ManyToOne(fetch = FetchType.LAZY)
//    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "message_id", nullable = false)
    @JsonIgnore()
    private Message message;

    @Transient
    private String attachmentContent;

    public Attachment() {}

    public Attachment(String fileUrl, String fileType, String fileName, Message message) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileName = fileName;
        this.message = message;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    public void setMessage(Message message) {
        this.message = message;
    }

    public String getAttachmentContent() {
        return attachmentContent;
    }

    public void setAttachmentContent(String attachmentContent) {
        this.attachmentContent = attachmentContent;
    }
}
