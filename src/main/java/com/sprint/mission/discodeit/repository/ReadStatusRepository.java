package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

    // 읽음상태 저장
    ReadStatus save(ReadStatus readStatus);

    // id로 조회
    Optional<ReadStatus> findById(UUID id);

    // 유저 아이디로 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 채널 아이디로 조회
    List<ReadStatus> findAllByChannelId(UUID channelId);

    // 수정
    void updateReadStatus(ReadStatus readStatus);

    // 읽은 메시지 취소 -> 다 읽지 않음으로
    void deleteById(UUID id);

    // 채널 아이디로 삭제
    void deleteAllByChannelId(UUID channelId);
}