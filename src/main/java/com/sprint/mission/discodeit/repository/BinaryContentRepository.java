package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BinaryContentRepository extends JpaRepository<BinaryContent, UUID> {

    // 다건 조회
    List<BinaryContent> findAllByIdIn(List<UUID> ids);
}
