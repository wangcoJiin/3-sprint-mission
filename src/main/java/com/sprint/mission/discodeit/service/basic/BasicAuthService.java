package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final Logger logger = Logger.getLogger(BasicAuthService.class.getName()); // 필드로 Logger 선언

    private  final UserStatusRepository fileUserStatusRepository;
    private final UserRepository userRepository;

    @Override
    public User login(LoginRequest request) {

        String username = request.username();
        String password = request.password();

        User user = userRepository.findUserByName(username)
                .orElseThrow(
                        () -> new NoSuchElementException("User with username " + username + " not found"));

        if (!Objects.equals(user.getPassword(), password)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다 ");
        }

        return user;
    }
}

