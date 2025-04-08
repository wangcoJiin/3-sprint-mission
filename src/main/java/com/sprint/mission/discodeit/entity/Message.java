package com.sprint.mission.discodeit.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message implements java.io.Serializable{

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private UUID channelId;
    private UUID senderId;
    private String messageContent;
    private LocalDateTime timestamp;


    public Message() {
    }

    public Message(UUID channelId, UUID senderId, String messageContent) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelId = channelId;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();;

    }

    public UUID getMessageId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public void updateSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public void updateMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void updateTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId=" + channelId +
                ", senderId=" + senderId +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
