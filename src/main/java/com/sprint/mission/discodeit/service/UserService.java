package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    // 유저 생성 (UserCreateRequest DTO 활용)
    UserDto create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileImage);

    // 유저 아이디 이용해서 조회
    UserDto find(UUID id);

    // 유저 전체 조회
    List<UserDto> findAll();

    UserDto update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);

    // id 이용해서 유저 삭제
    void delete(UUID id);
}