package com.sprint.mission.discodeit.exception.channel;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class ChannelException extends DiscodeitException {

    public ChannelException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details) {
        super(errorCode, details);
    }

    public ChannelException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ChannelException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    public ChannelException(ErrorCode errorCode, String customMessage) {
        super(errorCode, customMessage);
    }

    public ChannelException(ErrorCode errorCode, String customMessage,
        Map<String, Object> details) {
        super(errorCode, customMessage, details);
    }
}
