package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    // create
    @Override
    @Transactional
    public BinaryContent create(BinaryContentCreateRequest request) {

        String fileName = request.fileName();
        String contentType = request.contentType();
        byte[] bytes = request.bytes();

        BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType);
        binaryContentStorage.put(binaryContent.getId(), bytes);

        return binaryContentRepository.save(binaryContent);
    }

    // find
    @Override
    @Transactional(readOnly = true)
    public BinaryContentDto find(UUID id) {
        BinaryContent binaryContent = binaryContentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("BinaryContentService: 해당하는 바이너리 컨텐츠가 없습니다."));

        return binaryContentMapper.toDto(binaryContent);
    }

    // findAll
    @Override
    @Transactional(readOnly = true)
    public List<BinaryContentDto> findAllByIdIn(List<UUID> ids) {
        return binaryContentRepository.findAllByIdIn(ids).stream()
                .map(binaryContentMapper::toDto)
                .toList();
    }

    // delete
    @Override
    @Transactional
    public void delete(UUID id) {
        binaryContentRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("BinaryContentService: 해당하는 바이너리 컨텐츠가 없습니다."));

        binaryContentRepository.deleteById(id);
    }
}
