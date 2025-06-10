package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.util.List;
import java.util.UUID;

public interface BinaryContentService {

    // create
    BinaryContent create(BinaryContentCreateRequest request);

    // find
    BinaryContentDto find(UUID id);

    // findAll
    List<BinaryContentDto> findAllByIdIn(List<UUID> ids);

    // delete
    void delete(UUID id);
}