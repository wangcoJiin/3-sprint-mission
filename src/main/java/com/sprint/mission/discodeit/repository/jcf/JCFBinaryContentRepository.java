package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
@Repository
public class JCFBinaryContentRepository implements BinaryContentRepository {

    private final Map<UUID, BinaryContent> binaryContents = new LinkedHashMap<>();

    // 저장
    @Override
    public BinaryContent save(BinaryContent binaryContent) {
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
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
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
