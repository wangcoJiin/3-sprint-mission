package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

@RequiredArgsConstructor
@Service
public class BasicChannelService implements ChannelService {

    //레포지토리 의존성
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;

    private static final Logger logger = Logger.getLogger(BasicChannelService.class.getName()); // 필드로 Logger 선언

    // 공개 채널 생성
    @Override
    public Channel createPublicChannel(PublicChannelCreateRequest request) {

        System.out.println("공개 채널 생성 메서드 들어옴");

        Channel channel = new Channel(
                request.name(),
                ChannelType.PUBLIC,
                request.description()
        );
        logger.info("채널 이름: " + request.name());

        if (channelRepository.findChannelUsingName(request.name()).isPresent()) {
            throw new IllegalArgumentException("채널 이름이 이미 존재합니다.");
        }

        System.out.println("채널 생성됨");

        channelRepository.save(channel);

        return channel;
    }

    // 비공개 채널 생성
    @Override
    public Channel createPrivateChannel(PrivateChannelCreateRequest request) {

        Channel channel = new Channel(
                null,
                ChannelType.PRIVATE,
                null
        );

        Channel createdChannel = channelRepository.save(channel);

        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId, createdChannel.getId(), Instant.MIN))
                .forEach(readStatusRepository::save);

        return createdChannel;
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    public List<ChannelDto> findAllByUserId(UUID userId) {

        List<UUID> participants = readStatusRepository.findAllByUserId(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();

        return channelRepository.findAll().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || participants.contains(channel.getId())
                )
                .map(channel -> {
                    Instant lastMessageAt = messageRepository.findAllByChannelId(channel.getId())
                            .stream()
                            .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                            .map(Message::getCreatedAt)
                            .findFirst()
                            .orElse(Instant.MIN);

                    List<UUID> participantIds = new ArrayList<>();
                    if (channel.getType().equals(ChannelType.PRIVATE)) {
                        readStatusRepository.findAllByChannelId(channel.getId())
                                .stream()
                                .map(ReadStatus::getUserId)
                                .forEach(participantIds::add);
                    }

                    return new ChannelDto(
                            channel.getId(),
                            channel.getType(),
                            channel.getName(),
                            channel.getDescription(),
                            participantIds,
                            lastMessageAt
                    );
                })
                .toList();

    }

    // id로 채널 조회
    @Override
    public ChannelDto find(UUID channelId) {
        return channelRepository.findById(channelId)
            .map(channel -> {
                // 최신 메시지 시간 조회
                Instant latestMessageTime = messageRepository.findAllByChannelId(channel.getId())
                        .stream()
                        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                        .map(Message::getCreatedAt)
                        .limit(1)
                        .findFirst()
                        .orElse(Instant.MIN);

                List<UUID> participantIds = new ArrayList<>();
                if (channel.getType().equals(ChannelType.PRIVATE)) {
                    readStatusRepository.findAllByUserId(channel.getId())
                            .stream()
                            .map(ReadStatus::getUserId)
                            .forEach(participantIds::add);
                }

                return new ChannelDto(
                        channel.getId(),
                        channel.getType(),
                        channel.getName(),
                        channel.getDescription(),
                        participantIds,
                        latestMessageTime
                );
            })
            .orElseThrow(
                    () -> new NoSuchElementException("해당 채널이 없습니다."));
    }

    // 채널 이름 수정
    @Override
    public Channel update(UUID channelId, PublicChannelUpdateRequest request) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        channel.updateChannelName(request.newName());
        channel.updateDescription(request.newDescription());
        channel.updateUpdatedAt(Instant.now());
        logger.info("ChannelService: 채널 이름과 설명 수정이 완료되었습니다.");

        return channelRepository.save(channel);
    }

    // 채널 삭제
    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException("해당하는 채널이 없습니다."));


        List<Message> messages = messageRepository.findAllByChannelId(channelId);
        for (Message message : messages) {
            messageRepository.deleteById(message.getId());
        }
        logger.info("ChannelService: 해당 채널의 메시지가 삭제되었습니다.");

        List<ReadStatus> readStatuses = readStatusRepository.findAllByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }
        logger.info("ChannelService: 해당 채널의 ReadStatus가 삭제되었습니다.");

        channelRepository.deleteById(channelId);
        logger.info("ChannelService: 채널이 삭제되었습니다.");
    }
}
