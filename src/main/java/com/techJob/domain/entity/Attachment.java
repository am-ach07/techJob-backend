package com.techJob.domain.entity;

import com.techJob.domain.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;
    @Column(nullable = false, unique = true, updatable = false)
    private String attachmentPublicID;
	@Enumerated(EnumType.STRING)
    private MessageType fileType;
    private String url;
    private Long size;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public MessageType getFileType() {
		return fileType;
	}
	public void setFileType(MessageType fileType) {
		this.fileType = fileType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
		this.size = size;
	}
	public String getAttachmentPublicID() {
		return attachmentPublicID;
	}
	public void setAttachmentPublicID(String attachmentPublicID) {
		this.attachmentPublicID = attachmentPublicID;
	}
    
    
    
    
    
    
    
    
    
}
