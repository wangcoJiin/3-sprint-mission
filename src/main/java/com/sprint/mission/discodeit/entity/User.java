package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 유저 이메일 필드 추가된 테스트용 유저 도메인
 */

@Getter
public class User implements java.io.Serializable {

    //serialVersionUID 필드 추가
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String userEmail;
    private String userPassword;
    private UUID profileId;

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public User(String name, String userEmail, String userPassword) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.name = name;
        this.userEmail = userEmail;
        this.userPassword = userPassword;

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

    public void updateName(String name) {
        this.name = name;
    }

    public void updateUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void updateUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public void updateProfileId(UUID profileId) {
        this.profileId = profileId;
    }


    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));
        return "User{" +
                "id=" + id +
                ", createdAt=" + formatter.format(createdAt) +
                ", updatedAt=" + formatter.format(updatedAt) +
                ", name='" + name + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", profileId=" + profileId +
                '}';
    }
}
