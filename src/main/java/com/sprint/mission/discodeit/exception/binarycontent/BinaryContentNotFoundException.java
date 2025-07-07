package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, Map.of("조회하려고 한 바이너리 컨텐츠 아이디: ", binaryContentId));
    }
}
