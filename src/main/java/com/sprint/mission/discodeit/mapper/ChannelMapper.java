package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.response.ChannelDto;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
//@RequiredArgsConstructor
public class ChannelMapper {

    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private static final Logger logger = Logger.getLogger(ChannelMapper.class.getName()); // 필드로 Logger 선언

    public ChannelDto toDto(Channel channel){
        logger.info("채널 매퍼 진입");

        List<UserDto> participants = readStatusRepository.findAllByChannelId(channel.getId()).stream()
                .map(ReadStatus::getUser)
                .map(userMapper::toDto)
                .toList();
        logger.info("유저 매퍼 이용해서 참여자 조회");

        Instant lastMessageAt = messageRepository.findFirstByChannelOrderByCreatedAtDesc(channel).stream()
                .map(Message::getCreatedAt)
                .findFirst().orElse(null);
        logger.info("메시지 레포지토리 이용해서 마지막 메시지 전송 시간 조회");

        return new ChannelDto(
                channel.getId(),
                channel.getType(),
                channel.getName(),
                channel.getDescription(),
                participants,
                lastMessageAt
        );
    }
}
