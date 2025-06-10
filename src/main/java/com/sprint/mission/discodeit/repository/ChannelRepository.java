package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    // 중복 검사
    boolean existsByName(String channelName);

    // 채널 이름으로 조회
    Optional<Channel> findChannelByName(String channelName);

}
