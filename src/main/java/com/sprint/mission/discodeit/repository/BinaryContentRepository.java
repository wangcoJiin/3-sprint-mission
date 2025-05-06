package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

    // 바이너리 파일 저장
    boolean saveBinaryContent(BinaryContent binaryContent);

//    // 첨부파일 저장
//    boolean saveAttachedFile(UUID userId, UUID messageId);

    // 파일 아이디로 (?) 찾기
    Optional<BinaryContent> findById(UUID id);

//    // 파일 전체 조회
//    List<BinaryContent> findByUserId(UUID userId);
//
//    List<BinaryContent> findByMessageId(UUID messageId);

    // 파일 삭제
    boolean deleteById(UUID id);
}
