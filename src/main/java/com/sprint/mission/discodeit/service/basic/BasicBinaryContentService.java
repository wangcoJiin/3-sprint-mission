package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicBinaryContentService implements BinaryContentService {

    private static final Logger logger = Logger.getLogger(BasicBinaryContentService.class.getName());

    private final BinaryContentRepository binaryContentRepository;

    // create
    @Override
    public boolean createBinaryContent(BinaryContentCreateRequest request) {
        BinaryContent binaryContent = new BinaryContent(request.userId(), request.messageId(), request.data());
        boolean success = binaryContentRepository.saveBinaryContent(binaryContent);

        if(!success){
            logger.warning("바이너리 컨텐츠 저장에 실패 했습니다.");
        }

        return success;
    }

    // find
    @Override
    public Optional<BinaryContent> findBinaryContentById(UUID id) {
        return binaryContentRepository.findById(id);
    }

    // findAll
    @Override
    public List<BinaryContent> findAllBinaryContent(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();

        for(UUID id : ids){
            Optional<BinaryContent> findContent = binaryContentRepository.findById(id);
            findContent.ifPresent(result::add);
        }
        return result;
    }

    // delete
    @Override
    public boolean deleteBinaryContent(UUID id) {
        return binaryContentRepository.deleteById(id);
    }
}
