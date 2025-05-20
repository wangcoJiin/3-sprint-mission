package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateReq;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성
    Message createMessage(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests);

    // 채널의 메시지 조회
    List<Message> findallByChannelId(UUID channelId);

    // 채널의 특정 메시지 조회
    Message getMessageById(UUID messageId);

    // 메시지 수정
    Message updateMessage(UUID messageId, MessageUpdateRequest request);

    // 메시지 삭제
    void deletedMessage(UUID messageId);

}
