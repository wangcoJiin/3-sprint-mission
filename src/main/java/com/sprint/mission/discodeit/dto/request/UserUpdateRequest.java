package com.sprint.mission.discodeit.dto.request;

import java.util.Optional;
import java.util.UUID;

public record UserUpdateRequest (
        UUID userId,
        String newName,
        String newEmail,
        Optional<ProfileImageCreateRequest> newProfileImage // 선택적 프로필 이미지
) {}
