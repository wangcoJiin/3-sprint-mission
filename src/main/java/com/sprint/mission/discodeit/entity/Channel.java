package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel implements java.io.Serializable{

    private UUID id;
    private Long createdAt;
    private Long updatedAt;
    private String channelName;
    private UUID adminId;
    private boolean lockState;
    private String password;
    private List<UUID> joiningUsers;

    public Channel() {
    }

    public Channel(String channelName, UUID adminId, boolean lockState, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.channelName = channelName;
        this.adminId = adminId;
        this.lockState = lockState;
        this.password = password;
        this.joiningUsers = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public String getChannelName() {
        return channelName;
    }

    public UUID getAdminId() {
        return adminId;
    }

    public boolean isLock() {
        return lockState;
    }

    public String getPassword() {
        return password;
    }

    public List<UUID> getJoiningUsers() {
        return joiningUsers;
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

    public void updateIsLock(boolean lockState) {
        this.lockState = lockState;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void addJoiningUser(UUID userId) {
        joiningUsers.add(userId);
    }

    public void removeJoiningUser(UUID userId) {
        joiningUsers.remove(userId);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", channelName=" + channelName + '\'' +
                ", lockState=" + lockState +
                '}';
    }
}
