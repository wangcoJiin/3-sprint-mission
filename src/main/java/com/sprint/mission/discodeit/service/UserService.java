package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DTO로 파라미터 받는 거 테스트용 서비스 인터페이스
 */

public interface UserService {

    //     유저 생성 (UserCreateRequest DTO 활용)
    User create(UserCreateRequest request, Optional<BinaryContentCreateRequest> optionalProfileImage);

    // 유저 아이디 이용해서 조회
    UserDto find(UUID id);

    // 유저 전체 조회
    List<UserDto> findAll();

    User update(UUID userId, UserUpdateRequest userUpdateRequest, Optional<BinaryContentCreateRequest> optionalProfileCreateRequest);

    // id 이용해서 유저 삭제
    boolean delete(UUID id);
}