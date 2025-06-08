package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

    private final BinaryContentMapper binaryContentMapper;
    private static final Logger logger = Logger.getLogger(UserMapper.class.getName()); // 필드로 Logger 선언


    public UserDto toDto (User user){
        logger.info("UserMapper 호출됨");
        logger.info("user.getProfile 결과: " + user.getProfile());
        logger.info("binaryContentMapper 결과: " + binaryContentMapper.toDto(user.getProfile()));

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                binaryContentMapper.toDto(user.getProfile()),
                user.getStatus().isOnline()
        );
    }
}
