package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@Getter
public class User extends BaseUpdatableEntity {

    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "password", length = 60, nullable = false)
    private String password;


    @JoinColumn(name = "profile_id")
    @OneToOne(optional = true, orphanRemoval = true, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private BinaryContent profile;

    // 이 양방향 연관 관계의 주인은 외래키를 가지고 있는 UserStatus에 있다
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatus status;


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void updateName(String newUsername) {
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
        }
    }

    public void updateEmail(String newEmail) {
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
        }
    }

    public void updatePassword(String newPassword) {
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
        }
    }

    public void updateProfile(BinaryContent newProfile) {
        if (newProfile != null && !newProfile.equals(this.profile)) {
            this.profile = newProfile;
        }
    }

    public void setStatus(UserStatus status) {
        this.status = status;
        if (status != null && status.getUser() != this) {
            status.setUser(this);
        }
    }

    @Override
    public String toString() {

        return "User{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
