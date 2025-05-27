package com.sprint.mission.discodeit.dto.response;

import java.util.Map;

public record ResponseMessage(
    int httpStatus,
    String message,
    Map<String, Object>result
) { }
