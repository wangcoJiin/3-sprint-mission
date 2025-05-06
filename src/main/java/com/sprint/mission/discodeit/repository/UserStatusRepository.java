package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    // 유저가 로그인 하면 접속 시간 저장
    boolean saveUserStatus(UserStatus userStatus);

    // 유저 상태 조회
    Optional<UserStatus> findStatus(UUID userId);

    // 상태 전체 조회
    List<UserStatus> findAllStatus();

    // 유저 상태 업데이트 (30초마다?)
    boolean updateUserStatus(UserStatus userStatus);

    // 유저를 오프라인 상태로 (활동 중인 유저에서 삭제)
    boolean deleteUserStatus(UUID userId);

}
