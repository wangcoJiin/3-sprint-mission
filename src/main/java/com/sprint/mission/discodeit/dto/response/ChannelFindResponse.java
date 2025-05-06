package com.sprint.mission.discodeit.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelFindResponse(
        UUID channelId,
        String channelName,
        Instant lastMessageTime,
        List<UUID> participantIds
){ }