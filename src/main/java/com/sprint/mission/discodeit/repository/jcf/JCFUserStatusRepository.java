package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> userStatusMap = new LinkedHashMap<>();


    // 저장
    @Override
    public UserStatus save(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    // 아이디로 조회
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(this.userStatusMap.get(id));
    }

    // 유저 아이디로 조회
    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return userStatusMap.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    // 전체 조회
    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatusMap.values());
    }

    // 아이디로 삭제
    @Override
    public void deleteById(UUID id) {
        this.userStatusMap.remove(id);
    }

    // 유저 아이디로 삭제
    @Override
    public void deleteByUserId(UUID userId) {
        this.findByUserId(userId)
                .ifPresent(userStatus -> this.deleteByUserId(userStatus.getId()));
    }

    // 존재 여부
    @Override
    public boolean existsById(UUID id) {
        return this.userStatusMap.containsKey(id);
    }
}
