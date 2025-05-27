package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {

    // 공개 채널
    Channel createPublicChannel(PublicChannelCreateRequest request);

    // 비공개 채널
    Channel createPrivateChannel(PrivateChannelCreateRequest request);

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    List<ChannelDto> findAllChannel(UUID userId);

    // 채널 이름으로 조회
    Optional<Channel> getChannelUsingName(String channelName);

    // 채널 아이디로 조회
    ChannelDto getChannelUsingId(UUID channelId);

    // 채널 이름 수정
    Channel updateChannelName(UUID channelId, PublicChannelUpdateRequest request);

    //채널 삭제
    void deleteChannel(UUID channelId);

}
