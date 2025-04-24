package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel implements java.io.Serializable{

    @Serial
    private static final long serialVersionUID = 1L;


    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String channelName;
    private UUID adminId;
    private boolean lock;
    private String password;
    private List<UUID> joiningUsers;

    public Channel() {
    }

    public Channel(String channelName, UUID adminId, boolean lock, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.adminId = adminId;
        this.lock = lock;
        this.password = password;
        this.joiningUsers = new ArrayList<>();
    }

    public void updateId(UUID id) {
        this.id = id;
    }

    public void updateAdminId(UUID adminId) {
        this.adminId = adminId;
    }

    public void updateCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void updateChannelName(String name) {
        this.channelName = name;
    }

    public void updateIsLock(boolean lock) {
        this.lock = lock;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", channelName=" + channelName + '\'' +
                ", lockState=" + lock +
                '}';
    }
}
