package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.MessageDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private static final Logger logger = Logger.getLogger(BasicMessageService.class.getName()); // 필드로 Logger 선언

    private static final String FILE_PATH = "message.ser";

    // 의존성
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final MessageMapper messageMapper;

    //메시지 생성
    @Override
    @Transactional
    public MessageDto create(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        User user = userRepository.findById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));

        // 프로필 이미지 처리 (기본 이미지 or 전달된 이미지)
        List<BinaryContent> attachmets = binaryContentCreateRequests.stream()
                .map(profileImage -> {
                    BinaryContent content = new BinaryContent(
                            profileImage.fileName(),
                            (long) profileImage.bytes().length,
                            profileImage.contentType()
                    );
                    binaryContentStorage.put(content.getId(), profileImage.bytes());
                    return binaryContentRepository.save(content);

                })
                .toList();

        Message newMessage = new Message(
                channel,
                user,
                request.content(),
                attachmets
        );
        messageRepository.save(newMessage);

        return messageMapper.toDto(newMessage);
    }

    // 채널 메시지 조회 (findAll 수정 버전)
    @Override
    @Transactional(readOnly = true)
    public List<Message> findAllByChannelId(UUID channelId) {

        return messageRepository.findAllByChannelId(channelId).stream()
                .toList();
    }

    // 메시지 아이디 이용한 조회
    @Override
    @Transactional(readOnly = true)
    public Message find(UUID messageId) {

        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다. " + messageId));
    }

    // 메시지 수정
    @Override
    @Transactional
    public MessageDto update(UUID messageId, MessageUpdateRequest request) {

        String newContent = request.newContent();
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다. " + messageId));

        message.updateContent(newContent);

        return messageMapper.toDto(message);
    }

    // 메시지 삭제
    @Override
    @Transactional
    public void delete(UUID messageId) {

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다. " + messageId));

        messageRepository.deleteById(messageId);

    }

}

