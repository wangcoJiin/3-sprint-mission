package com.sprint.mission.discodeit.dto.response;

import java.util.UUID;

/**
 * 유저 조회 응답 DTO
 */

public record UserResponse(
        UUID id,
        String name,
        String userEmail,
        UUID profileId,
        String status
) { }