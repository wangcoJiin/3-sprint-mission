package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    // 유저 생성
    User createUser(String name);

    // 기존 유저 리스트에 새로운 유저 추가
    void addUserToRepository(User user);

    // 유저 아이디 이용해서 조회
    User getUserById(UUID id);

    // 유저 이름 이용해서 조회
    List<User> searchUsersByName(String name);

    List<User> getAllUsers();

    // 유저 id로 이름과 활동상태 둘 다 변경
    User updateUser(UUID id, String name, String connectState);

    // 유저 이름 변경
    boolean updateUserName(UUID id, String newName);

    // 유저 이름으로 활동상태 변경
    boolean updateConnectState(String name, String connectState);

    // id 이용해서 유저 삭제
    boolean deleteUserById(UUID id);
}
