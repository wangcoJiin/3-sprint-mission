package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    // 메시지 생성
    @Override
    public boolean saveMessage(Message message) {
        messages.put(message.getMessageId(), message);

        return true;
    }

    // 메시지에 첨부파일 아이디 추가
    @Override
    public boolean addAttachedFileId(UUID messageId, UUID attachedFileId) {
        Message message = findMessageById(messageId);
        message.getAttachedFileIds().add(attachedFileId);

        messages.put(message.getMessageId(), message);
        return true;
    }

    // 메시지 내용 수정
    @Override
    public boolean updateMessage(UUID messageId, String newMessageContent) {
        Message message = findMessageById(messageId);
        message.updateMessageContent(newMessageContent);
        message.updateUpdatedAt(Instant.now());

        return true;
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 아이디로 메시지 조회
    @Override
    public Message findMessageById(UUID messageId) {
        return messages.get(messageId);
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> findMessageByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 특정 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId) {
        return  messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId) {
        messages.remove(messageId);
        return true;
    }
}
