package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // create
    BinaryContent create(BinaryContentCreateRequest request);

    // find
    BinaryContent find(UUID id);

    // findAll
    List<BinaryContent> findAllByIdIn(List<UUID> ids);

    // delete
    boolean delete(UUID id);
}