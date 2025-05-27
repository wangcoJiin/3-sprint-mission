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
    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;

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

    public void updateUserName(String username) {
        this.username = username;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
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
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", profileId=" + profileId +
                '}';
    }
}
