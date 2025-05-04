package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

/*
 유저 생성 응답 DTO
 */
public record UserCreateResponse(
        UUID id,
        String name,
        String userEmail,
        UUID profileId,
        String userStatus
) { }
