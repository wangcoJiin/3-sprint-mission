package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private static final Logger logger = Logger.getLogger(BasicBinaryContentService.class.getName());

    private final BinaryContentRepository binaryContentRepository;

    // create
    @Override
    public BinaryContent create(BinaryContentCreateRequest request) {

        String fileName = request.fileName();
        String cotentType = request.contentType();
        byte[] bytes = request.bytes();


        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, cotentType, bytes);

        return binaryContentRepository.save(binaryContent);
    }

    // find
    @Override
    public BinaryContent find(UUID id) {
        return binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당하는 바이너리 컨텐츠가 없습니다."));
    }

    // findAll
    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .toList();
    }

    // delete
    @Override
    public boolean delete(UUID id) {
        binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당하는 바이너리 컨텐츠가 없습니다."));

        return binaryContentRepository.deleteById(id);
    }
}
