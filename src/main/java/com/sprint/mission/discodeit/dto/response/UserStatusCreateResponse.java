package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.UUID;

/**
 * 유저 접속 상태 생성 결과 DTO
 */
public record UserStatusCreateResponse(
        UUID statusId,
        UUID userId,
        Instant createdAt,
        Instant updatedAt
) { }