package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channelRepository.ser";

    private static final Logger logger = Logger.getLogger(FileChannelRepository.class.getName());

    private final Map<UUID, Channel> channels = loadChannelFromFile();


    // 채널 정보를 파일로 저장
    private boolean saveChannelToFile(Map<UUID, Channel> channels) {

        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {

            oos.writeObject(channels);
            return true;

        } catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, FILE_PATH + "경로에 파일을 생성할 수 없습니다: ", e);
            return false;
        } catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, "채널 파일 저장 중 오류 발생", e);
            return false;
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

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

    // 채널에 참여자 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().add(userId);
        channel.updateUpdatedAt(Instant.now());

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
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
        channel.updateUpdatedAt(Instant.now());

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

    // 채널 공개상태로 수정
    @Override
    public boolean channelUnLocking(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(false);
        channel.updatePassword("");
        channel.updateUpdatedAt(Instant.now());

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

    // 채널 비공개상태로 수정
    @Override
    public boolean channelLocking(UUID channelId, String password) {
        Channel channel = findChannelUsingId(channelId);
        channel.updateIsLock(true);
        channel.updatePassword(password);
        channel.updateUpdatedAt(Instant.now());

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId) {
        Channel channel = findChannelUsingId(channelId);
        channels.remove(channelId);

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

    // 채널의 참여자 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelUsingId(channelId);
        channel.getJoiningUsers().remove(userId);
        channel.updateUpdatedAt(Instant.now());

        // 채널 저장 상태 확인
        boolean success = saveChannelToFile(channels);
        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + channel.getId());
        }
        return true;
    }

}
