package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
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

        channelRepository.saveChannel(channel);

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

//        // 채널 참여자 리스트에 추가
//        for (UUID participantId : request.participantIds()) {
//            channel.getJoiningUsers().add(participantId);
//            channelRepository.saveChannel(channel);
//        }
//
//        // 채널 참여자 별 readStatus 생성하기
//        for (UUID participantId : channel.getJoiningUsers()) {
//            ReadStatus readStatus = new ReadStatus(participantId, channel.getId(), Instant.now());
//            readStatusRepository.saveReadStatus(readStatus);
//        }

        request.participantIds().stream()
                .map(userId -> new ReadStatus(userId, channel.getId(), Instant.now()))
                .forEach(readStatusRepository::saveReadStatus);

        Channel createdChannel = channelRepository.saveChannel(channel);

        return createdChannel;
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    public List<ChannelDto> findAllChannel(UUID userId) {

        List<Channel> channels = channelRepository.findAllChannels();

        List<UUID> participants = readStatusRepository.findUserReadStatus(userId).stream()
                .map(ReadStatus::getChannelId)
                .toList();


        return channelRepository.findAllChannels().stream()
                .filter(channel ->
                        channel.getType().equals(ChannelType.PUBLIC)
                                || participants.contains(channel.getId())
                )
                .map(channel -> {

                    // 최신 메시지 시간 조회
                    Instant latestMessageTime = messageRepository.findMessageByChannel(channel.getId())
                            .stream()
                            .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                            .map(Message::getCreatedAt)
                            .limit(1)
                            .findFirst()
                            .orElse(Instant.MIN);

                    List<UUID> participantIds = new ArrayList<>();
                    if (channel.getType().equals(ChannelType.PRIVATE)) {
                        readStatusRepository.findUserReadStatus(channel.getId())
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
                .toList();
    }

    // 이름만으로 채널 조회
    @Override
    public Optional<Channel> getChannelUsingName(String channelName) {

        return channelRepository.findChannelUsingName(channelName);
    }

    // id로 채널 조회
    @Override
    public ChannelDto getChannelUsingId(UUID channelId) {
        return channelRepository.findChannelUsingId(channelId)
            .map(channel -> {
                // 최신 메시지 시간 조회
                Instant latestMessageTime = messageRepository.findMessageByChannel(channel.getId())
                        .stream()
                        .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                        .map(Message::getCreatedAt)
                        .limit(1)
                        .findFirst()
                        .orElse(Instant.MIN);

                List<UUID> participantIds = new ArrayList<>();
                if (channel.getType().equals(ChannelType.PRIVATE)) {
                    readStatusRepository.findUserReadStatus(channel.getId())
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
    public Channel updateChannelName(UUID channelId, PublicChannelUpdateRequest request) {

        Channel channel = channelRepository.findChannelUsingId(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));

        if (channel.getType().equals(ChannelType.PRIVATE)) {
            throw new IllegalArgumentException("비공개 채널은 수정할 수 없습니다.");
        }

        channel.updateChannelName(request.newName());
        channel.updateDescription(request.newDescription());
        channel.updateUpdatedAt(Instant.now());
        logger.info("ChannelService: 채널 이름과 설명 수정이 완료되었습니다.");

        return channelRepository.saveChannel(channel);
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findChannelUsingId(channelId)
                .orElseThrow(
                        () -> new NoSuchElementException("해당하는 채널이 없습니다."));


        List<Message> messages = messageRepository.findMessageByChannel(channelId);
        for (Message message : messages) {
            messageRepository.deletedMessage(message.getId());
        }
        logger.info("ChannelService: 해당 채널의 메시지가 삭제되었습니다.");

        List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannelId(channelId);
        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteReadStatusById(readStatus.getId());
        }
        logger.info("ChannelService: 해당 채널의 ReadStatus가 삭제되었습니다.");

        channelRepository.deleteChannel(channelId);
        logger.info("ChannelService: 채널이 삭제되었습니다.");
    }
}
