package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UserRepository {

    // 유저 저장
    void saveUser(User user);

    // 전체 유저 조회
    List<User> findUserAll();

    // 유저 조회 (id)
    User findUserById(UUID userId);

    // 유저 조회 (이름)
    List<User> findUserByName(String userName);

    // 유저 이름 수정
    boolean updateUserName(User user, String newName);

    // 유저 활동상태 수정
    boolean updateConnectState(User user, String connectState);

    // 유저 삭제
    void deleteUser(UUID userId);
}
