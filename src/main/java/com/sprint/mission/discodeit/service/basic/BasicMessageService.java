package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
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

    //메시지 생성
    @Override
    public Message createMessage(MessageCreateRequest request, List<BinaryContentCreateRequest> binaryContentCreateRequests) {

        User user = userRepository.findUserById(request.authorId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 유저가 존재하지 않습니다."));

        Channel channel = channelRepository.findChannelUsingId(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("MessageService: 채널이 존재하지 않습니다."));


        List<UUID> attachmentIds = binaryContentCreateRequests.stream()
                .map(attachmentRequest -> {
                    String fileName = attachmentRequest.fileName();
                    String contentType = attachmentRequest.contentType();
                    byte[] bytes = attachmentRequest.bytes();

                    BinaryContent newBinaryContent = new BinaryContent(fileName, contentType, bytes);
                    BinaryContent binaryContent = binaryContentRepository.saveBinaryContent(newBinaryContent);

                    return binaryContent.getId();
                })
                .toList();

        Message newMessage = new Message(request.channelId(), request.authorId(), request.content(), attachmentIds);
        Message created = messageRepository.saveMessage(newMessage);

        if (created == null) {
            throw new IllegalStateException("MessageService: 메시지 저장 실패");
        }

        return messageRepository.saveMessage(newMessage);
    }

    // 채널 메시지 조회 (findAll 수정 버전)
    @Override
    public List<Message> findallByChannelId(UUID channelId) {

        return messageRepository.findMessageByChannel(channelId).stream()
                .toList();
    }

    // 메시지 아이디 이용한 조회
    @Override
    public Message getMessageById(UUID messageId) {

        return messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));
    }

    // 메시지 수정
    @Override
    public Message updateMessage(UUID messageId, MessageUpdateRequest request) {

        Message message = messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));

        message.updateContent(request.newContent());

        return messageRepository.saveMessage(message);
    }

    // 메시지 삭제
    @Override
    public void deletedMessage(UUID messageId) {

        Message message = messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new NoSuchElementException("MessageService: 메시지가 존재하지 않습니다."));

        // 연관 첨부파일 삭제
        message.getAttachmentIds()
                .forEach(binaryContentRepository::deleteById);

        messageRepository.deletedMessage(messageId);

    }

}

