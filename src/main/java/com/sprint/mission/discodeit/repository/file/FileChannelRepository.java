package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channelRepository.ser";

    private Map<UUID, Channel> channels = loadChannelFromFile();

    // 채널 정보를 파일로 저장
    private void saveChannelToFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 채널 파일 읽어오기
    private Map<UUID, Channel> loadChannelFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("채널 파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }


    // 채널 저장
    @Override
    public boolean saveChannel(Channel channel) {
        channels.put(channel.getId(), channel);
        addUserToChannel(channel.getId(), channel.getAdminId());
        saveChannelToFile(channels);

        return true;
    }

    // 채널에 참여자 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().add(userId);
        channel.updateUpdatedAt(System.currentTimeMillis());
        saveChannelToFile(channels);

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
        channel.updateUpdatedAt(System.currentTimeMillis());
        saveChannelToFile(channels);

        return true;
    }

    // 채널 공개상태로 수정
    @Override
    public boolean channelUnLocking(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(false);
        channel.updatePassword("");
        channel.updateUpdatedAt(System.currentTimeMillis());
        saveChannelToFile(channels);

        return true;
    }

    // 채널 비공개상태로 수정
    @Override
    public boolean channelLocking(UUID channelId, String password) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(true);
        channel.updatePassword(password);
        channel.updateUpdatedAt(System.currentTimeMillis());
        saveChannelToFile(channels);

        return true;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channels.remove(channelId);
        saveChannelToFile(channels);

        return true;
    }

    // 채널의 참여자 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().remove(userId);
        channel.updateUpdatedAt(System.currentTimeMillis());
        saveChannelToFile(channels);

        return true;
    }

}
