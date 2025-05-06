package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record UserProfileImageUpdateRequest (
        UUID userId,
        byte[] newImageData
)
{}
