package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> users = new LinkedHashMap<>();


    // 유저 저장
    @Override
    public boolean saveUser(User user) {
        users.put(user.getId(), user);

        return true;
    }

    //유저 read (all)
    @Override
    public List<User> findUserAll() {
        return new ArrayList<>(users.values());
    }

    // 유저 read (id)
    @Override
    public User findUserById(UUID userId) {
        return users.get(userId);
    }

    // 유저 read (name)
    @Override
    public List<User> findUserByName(String userName) {
        return users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(userName))
                .collect(Collectors.toList());
    }

    // 유저 이름 수정
    @Override
    public boolean updateUserName(User user, String newName){
        user.updateName(newName);
        user.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    // 유저 활동상태 수정
    @Override
    public boolean updateConnectState(User user, String connectState){
        user.updateConnectState(connectState);
        user.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    //유저 삭제
    @Override
    public boolean deleteUser(UUID userId) {
        users.remove(userId);

        return true;
    }

}
