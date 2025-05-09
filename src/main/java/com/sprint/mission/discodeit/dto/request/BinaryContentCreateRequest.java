package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record BinaryContentCreateRequest(
        String fileName,
        String contentType,
        byte[] data
) { }
