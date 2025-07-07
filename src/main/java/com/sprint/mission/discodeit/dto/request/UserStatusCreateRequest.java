package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

public record UserStatusCreateRequest(

        @NotNull(message = "사용자 Id는 필수입니다.")
        UUID userId,

        @NotNull(message = "활동 시간은 필수입니다.")
        Instant lastActiveAt
) { }
