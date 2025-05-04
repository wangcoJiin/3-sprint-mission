package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.UUID;

// 수정 불가능한 객체, 따라서 수정한다는 것은 곧 삭제하고 다시 생성한다는 의미임.
@Getter
public class BinaryContent implements Serializable {

    private final UUID id;
    private final Instant createdAt;
    private final UUID userId;
    private final UUID messageId;
    private final byte[] data;

    public BinaryContent(UUID userId, UUID messageId, byte[] data) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.userId = userId;
        this.messageId = messageId;
        this.data = data;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "BinaryContent{" +
                "id=" + id +
                ", createdAt=" + formatter.format(createdAt) +
                ", userId=" + userId +
                ", messageId=" + messageId +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
