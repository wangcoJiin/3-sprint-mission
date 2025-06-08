package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final Logger logger = Logger.getLogger(BasicAuthService.class.getName()); // 필드로 Logger 선언

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto login(LoginRequest request) {

        String username = request.username();
        String password = request.password();

        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new NoSuchElementException("AuthService: 유저를 찾을 수 없습니다. " + username));

        if (!Objects.equals(user.getPassword(), password)){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다 ");
        }

        return userMapper.toDto(user);
    }
}

