package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {

    private Map<UUID, User> users = new HashMap<>();

    // 유저 생성
    @Override
    public User createUser(String name) {
        User newUser = new User(name, "초기값");
//        newUser.updateId(userId);
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    // 기존 유저 리스트에 새로운 유저 추가
    @Override
    public void addUserToRepository(User user) {
        users.put(user.getId(), user);
    }

    // 유저 아이디 이용해서 조회
    @Override
    public User getUserById(UUID id) {
        return users.values().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 유저 이름 이용해서 조회
    @Override
    public List<User> searchUsersByName(String name) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }

    // 전체 유저 정보 조회
    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // 유저 이름과 활동상태 둘 다 변경
    @Override
    public User updateUser(UUID id, String name, String connectState) {
        User user = getUserById(id);
        if (user != null) {
            user.updateName(name);
            user.updateConnectState(connectState);
            user.updateUpdatedAt(System.currentTimeMillis());
        }
        return user;
    }

    // 유저 활동상태 변경
    @Override
    public boolean updateConnectState(String name, String newState) {
        List<User> users = searchUsersByName(name);
        if (users.size() > 1) {
            System.out.println("두 명 이상의 유저가 조회됐습니다.");
            return false;
        }
        if (users.isEmpty()) {
            System.out.println("조회된 유저가 없습니다.");
            return false;
        } else {
            for (User user : users) {
                user.updateConnectState(newState);
                user.updateUpdatedAt(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    // 유저 이름 변경
    @Override
    public boolean updateUserName(UUID id, String newName) {
        User user = getUserById(id);
        if (user != null) {
            user.updateName(newName);
            user.updateUpdatedAt(System.currentTimeMillis());
            return true;
        }
        return false;
    }

    // 유저 삭제
    @Override
    public boolean deleteUserById(UUID id) {
        User user = users.get(id);
        if (user !=null) {
            users.remove(id);
            System.out.println("유저가 삭제되었습니다.");
            return true;
        } else {
            System.out.println("조회된 유저가 없습니다.");
            return false;
            }
        }
}