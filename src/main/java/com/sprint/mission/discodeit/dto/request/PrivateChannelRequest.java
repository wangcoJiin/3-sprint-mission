package com.sprint.mission.discodeit.dto.request;

import java.util.List;
import java.util.UUID;

public record PrivateChannelRequest (
        String channelName,
        UUID adminId,
        String password,
        List<UUID> participantIds
){ }