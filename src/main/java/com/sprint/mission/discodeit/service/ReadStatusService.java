package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ReadStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.ReadStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusService {

    // 생성
    ReadStatus create(ReadStatusCreateRequest request);

    // 조회
    Optional<ReadStatus> find(UUID id);

    // 유저 아이디로 조회
    List<ReadStatus> findAllByUserId(UUID userId);

    // 수정
    ReadStatus update(UUID readStatusId, ReadStatusUpdateRequest request);

    // 삭제
    void delete(UUID id);
}
