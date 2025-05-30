package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 이메일 필드 생긴 테스트용 유저 레포지토리 인터페이스
 */

public interface UserRepository {

    // 유저 저장
    User save(User user);

    // 전체 유저 조회
    List<User> findAll();

    // 유저 조회 (id)
    Optional<User> findById(UUID userId);

    // 유저 조회 (이름)
    Optional<User> findByUsername(String userName);

    // 유저 조회 (이메일)
    Optional<User> findUserByEmail(String userEmail);

    // 유저 삭제
    void deleteUser(UUID userId);
}
