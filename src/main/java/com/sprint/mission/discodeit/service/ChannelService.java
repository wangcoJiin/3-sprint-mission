package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // 채널 생성
    Channel createChannel(String channelName, UUID adminId, boolean isprivate, String password);

    // 채널에 유저 추가
    boolean addUserToChannel(UUID channelId, UUID userId, String password);

    //전체 채널 조회
    List<Channel> getAllChannels();

    // 채널 이름으로 조회
    List<Channel> getChannelUsingName(String channelName);

    // 채널 아이디로 조회
    Channel getChannelUsingId(UUID channelId);

    // 체널 이름 수정
    boolean updateChannelName(UUID channelId, String channelName, UUID userId, String password, String newChannelName);

    //채널 공개 유무 수정
    boolean updateChannelPrivateState(UUID channelId, String channelName, UUID userId, String password, boolean isprivate);

    //채널 삭제
    boolean deleteChannel(UUID channelId, String channelName, UUID userId, String password);

    //채널 삭제
    boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password);
}
