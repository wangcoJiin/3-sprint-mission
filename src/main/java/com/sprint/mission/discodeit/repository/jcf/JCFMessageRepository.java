package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    // 메시지 생성
    @Override
    public Message save(Message message) {
        messages.put(message.getId(), message);

        return message;
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }


    // 아이디로 메시지 조회
    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.of(messages.get(messageId));
    }


    // 특정 채널의 메시지 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public void deleteById(UUID id) {
        this.messages.remove(id);
    }

}
