package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public record ChannelUpdateNameRequest(
    UUID channelId,
    String newName
) { }
