package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    // 메시지 생성
    Message CreateMessage(UUID channelId, UUID senderId, String messageContent);

    // 메시지 수정
    boolean updateMessage(UUID messageId, UUID senderId, String newMessageContent);

    //전체 메시지 조회
    List<Message> getAllMessage();

    // 채널의 메시지 조회
    List<Message> getMessageByChannel(UUID channelId);

    // 채널의 특정 메시지 조회
    Message getMessageById(UUID messageId, UUID channelId);

    // 유저가 보낸 메시지 조회
    List<Message> userMessage(UUID senderId);

    // 메시지 삭제
    boolean deletedMessage(UUID messageId, UUID senderId);

}
