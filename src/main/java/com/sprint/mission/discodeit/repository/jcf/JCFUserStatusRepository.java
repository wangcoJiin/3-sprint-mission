package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;

public class JCFUserStatusRepository implements UserStatusRepository {

    private final Map<UUID, UserStatus> userStatusMap = new LinkedHashMap<>();


    // 저장
    @Override
    public boolean saveUserStatus(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return true;
    }

    // 아이디로 조회
    @Override
    public Optional<UserStatus> findStatus(UUID userId) {
        return userStatusMap.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    // 전체 조회
    @Override
    public List<UserStatus> findAllStatus() {
        return new ArrayList<>(userStatusMap.values());
    }

    // 수정
    @Override
    public boolean updateUserStatus(UserStatus userStatus) {
        userStatusMap.put(userStatus.getId(), userStatus);
        return true;
    }

    // 삭제
    @Override
    public boolean deleteUserStatus(UUID userId) {
        Optional<UserStatus> foudStatus = userStatusMap.values().stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();

        if (foudStatus.isPresent()) {
            userStatusMap.remove(foudStatus.get().getId());
            return true;
        }
        return false;
    }
}
