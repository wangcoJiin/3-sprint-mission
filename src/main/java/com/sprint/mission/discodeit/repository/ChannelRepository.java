package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelRepository {

    // 채널 생성
    boolean saveChannel(Channel channel);

    // 채널에 참여자 추가
    boolean addUserToChannel(UUID channelId, UUID userId);

    //전체 채널 조회
    List<Channel> findAllChannels();

    // 채널 이름으로 조회
    List<Channel> findChannelUsingName(String channelName);

    // 채널 아이디로 조회
    Channel findChannelUsingId(UUID channelId);

    // 체널 이름 수정
    boolean updateChannelName(UUID channelId, String newChannelName);

    // 채널 공개 상태로 수정
    boolean channelUnLocking(UUID channelId);

    // 채널 비공개 상태로 수정
    boolean channelLocking(UUID channelId, String password);

    //채널 삭제
    boolean deleteChannel(UUID channelId);

    //채널의 유저 삭제
    boolean deleteUserInChannel(UUID channelId, UUID userId);

}
