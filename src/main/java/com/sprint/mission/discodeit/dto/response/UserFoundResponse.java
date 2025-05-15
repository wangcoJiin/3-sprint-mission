package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.OnlineStatus;

import java.util.UUID;

public record UserFoundResponse (
        UUID id,
        String name,
        String userEmail,
        UUID profileId,
        OnlineStatus state
)
{ }

