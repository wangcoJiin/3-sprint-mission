package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusService implements ReadStatusRepository {

    private final Map<UUID, ReadStatus> readStatusMap = new LinkedHashMap<>();


    // 저장
    @Override
    public boolean saveReadStatus(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return true;
    }

    // 유저 아이디 +  채널 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusByUserId(UUID userId, UUID channelId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId)
                && readStatus.getChannelId().equals(channelId))
                .findFirst();
    }

    // 아이디로 조회
    @Override
    public Optional<ReadStatus> findReadStatusById(UUID id) {
        return Optional.ofNullable(readStatusMap.get(id));
    }

    // 유저 아이디로 조회
    @Override
    public List<ReadStatus> findUserReadStatus(UUID userId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // 채널 아이디로 조회
    @Override
    public List<ReadStatus> findReadStatusByChannelId(UUID channelId) {
        return readStatusMap.values().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 수정
    @Override
    public boolean updateReadStatus(ReadStatus readStatus) {
        readStatusMap.put(readStatus.getId(), readStatus);
        return true;
    }

    // 삭제
    @Override
    public boolean deleteReadStatusById(UUID id) {
        readStatusMap.remove(id);
        return true;
    }
}
