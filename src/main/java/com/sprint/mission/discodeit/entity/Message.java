package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Getter
public class Message implements java.io.Serializable{

    //serialVersionUID 필드 추가
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID Id;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID channelId;
    private UUID authorId;
    private String content;
    private List<UUID> attachmentIds;


    public Message() {
    }

    public Message(UUID channelId, UUID authorId, String content, List<UUID> attachmentIds) {
        this.Id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.content = content;
        this.channelId = channelId;
        this.authorId = authorId;
        this.attachmentIds = attachmentIds;
    }

    public void updateId(UUID Id) {
        this.Id = Id;
    }

    public void updateCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public void updateAuthorId(UUID authorId) {
        this.authorId = authorId;
    }


    public void updateContent(String newContent) {
        boolean anyValueUpdated = false;
        if (newContent != null && !newContent.equals(this.content)) {
            this.content = newContent;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "Message{" +
                "Id=" + Id +
                ", createdAt=" + formatter.format(createdAt) +
                ", updatedAt=" + formatter.format(updatedAt) +
                ", channelId=" + channelId +
                ", authorId=" + authorId +
                ", content='" + content + '\'' +
                '}';
    }
}
