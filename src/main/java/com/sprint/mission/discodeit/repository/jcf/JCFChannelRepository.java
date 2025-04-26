package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {

    private final Map<UUID, Channel> channels = new LinkedHashMap<>();

    // 채널 저장
    @Override
    public boolean saveChannel(Channel channel) {
        channels.put(channel.getId(), channel);
        addUserToChannel(channel.getId(), channel.getAdminId());

        return true;
    }

    // 채널에 참여자 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().add(userId);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    // 전체 채널 조회
    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channels.values());
    }

    // 이름으로 채널 조회
    @Override
    public List<Channel> findChannelUsingName(String channelName) {
        return channels.values().stream()
                .filter(channel -> channel.getChannelName().equalsIgnoreCase(channelName))
                .collect(Collectors.toList());
    }

    // 아이디로 채널 조회
    @Override
    public Channel findChannelUsingId(UUID channelId) {
        return channels.get(channelId);
    }

    // 채널 이름 수정
    @Override
    public boolean updateChannelName(UUID channelId, String newChannelName) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateChannelName(newChannelName);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    // 채널 공개상태로 수정
    @Override
    public boolean channelUnLocking(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(false);
        channel.updatePassword("");
        channel.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    // 채널 비공개상태로 수정
    @Override
    public boolean channelLocking(UUID channelId, String password) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(true);
        channel.updatePassword(password);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channels.remove(channelId);

        return true;
    }

    // 채널의 참여자 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().remove(userId);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return true;
    }
}
