package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFUserRepository implements UserRepository {

    private final Map<UUID, User> users = new LinkedHashMap<>();

    // 유저 저장
    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    // 전체 유저 조회
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    // 특정 유저 조회
    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.of(users.get(userId));
    }

    // 이름으로 조회
    @Override
    public Optional<User> findByUsername(String userName) {
        return users.values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(userName))
                .findFirst();
    }

    // 이메일로 조회
    @Override
    public Optional<User> findUserByEmail(String userEmail) {
        return users.values().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(userEmail))
                .findFirst();
    }

    // 유저 삭제
    @Override
    public void deleteUser(UUID userId) {
        users.remove(userId);
    }
}
