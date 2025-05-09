package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public record ReadStatusUpdateRequest (
        UUID id,
        Instant updatedAt
){ }
