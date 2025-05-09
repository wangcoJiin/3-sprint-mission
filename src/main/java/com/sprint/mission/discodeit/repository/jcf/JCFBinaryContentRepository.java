package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;

import java.util.*;
import java.util.logging.Logger;

public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> binaryContents = new LinkedHashMap<>();

    // 저장
    @Override
    public BinaryContent saveBinaryContent(BinaryContent binaryContent) {
        binaryContents.put(binaryContent.getId(), binaryContent);
        return binaryContent;
    }

    // 조회
    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return Optional.ofNullable(binaryContents.get(id));
    }

    // 다건조회
    @Override
    public List<BinaryContent> findAllByIds(List<UUID> ids) {
        return binaryContents.values().stream()
                .filter(binaryContent -> ids.contains(binaryContent.getId()))
                .toList();
    }

    //삭제
    @Override
    public boolean deleteById(UUID id) {
        binaryContents.remove(id);
        return true;
    }
}
