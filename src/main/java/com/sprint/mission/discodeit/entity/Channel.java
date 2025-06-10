package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "channels")
@Getter
public class Channel extends BaseUpdatableEntity {

    @Column(name = "name", length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ChannelType type;

    @Column(name = "description", length = 500)
    private String description;


    public Channel(String name, ChannelType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    public void updateChannelName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {

        return "Channel{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}

