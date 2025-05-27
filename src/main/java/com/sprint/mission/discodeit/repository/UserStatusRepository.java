package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

    // 유저가 로그인 하면 접속 시간 저장
    UserStatus save(UserStatus userStatus);

    // 유저 상태 조회
    Optional<UserStatus> findByUserId(UUID userId);

    // 아이디로 조회
    Optional<UserStatus> findById(UUID id);

    // 상태 전체 조회
    List<UserStatus> findAll();

//    // 유저 상태 업데이트
//    boolean updateUserStatus(UserStatus userStatus);
//
    boolean existsById(UUID id);

    void deleteById(UUID id);

    // 아이디로 삭제
    void deleteByUserId(UUID userId);

}
