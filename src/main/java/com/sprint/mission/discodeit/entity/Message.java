package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Message implements java.io.Serializable{

    //serialVersionUID 필드 추가
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID messageId;
    private Long createdAt;
    private Long updatedAt;
    private UUID channelId;
    private UUID senderId;
    private String messageContent;
    private LocalDateTime timestamp;


    public Message() {
    }

    public Message(UUID channelId, UUID senderId, String messageContent) {
        this.messageId = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelId = channelId;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();;

    }

//    public UUID getMessageId() {
//        return messageId;
//    }

    public void updateId(UUID messageId) {
        this.messageId = messageId;
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
                "messageId=" + messageId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", channelId=" + channelId +
                ", senderId=" + senderId +
                ", messageContent='" + messageContent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
