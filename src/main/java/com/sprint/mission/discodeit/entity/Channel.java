package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements java.io.Serializable{

    @Serial
    private static final long serialVersionUID = 1L;


    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private ChannelType type;
    private String description;

    public Channel() {
    }

    public Channel(String name, ChannelType type, String description) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));

        return "Channel{" +
                "id=" + id +
                ", createdAt='" + formatter.format(createdAt) + '\'' +
                ", updatedAt='" + formatter.format(updatedAt) + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}
