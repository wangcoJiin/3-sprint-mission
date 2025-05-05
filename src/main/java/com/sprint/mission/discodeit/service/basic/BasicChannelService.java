package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateNameRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelFindResponse;
import com.sprint.mission.discodeit.entity.Channel;
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

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID adminId, boolean lockState, String password) {
        System.out.println("채널 생성중");
        Channel newChannel = new Channel(channelName, adminId, lockState, password);
        channelRepository.saveChannel(newChannel);
        // 채널 생성과 동시에 참여자에 관리자도 추가
        channelRepository.addUserToChannel(newChannel.getId(), adminId);

        return newChannel;
    }

    // 공개 채널 생성
    @Override
    public Channel createPublicChannel(PublicChannelRequest request) {

        Channel channel = new Channel(
                request.channelName(),
                request.adminId(),
                false,
                ""
        );

        channelRepository.saveChannel(channel);
        channelRepository.addUserToChannel(channel.getId(), request.adminId());

        return channel;
    }

    // 비공개 채널 생성
    @Override
    public Channel createPrivateChannel(PrivateChannelRequest request) {

        Channel channel = new Channel(
                request.channelName(),
                request.adminId(),
                true,
                request.password()
        );

        channelRepository.saveChannel(channel);
        channelRepository.addUserToChannel(channel.getId(), request.adminId());


        // 채널 참여자 별 readStatus 생성하기
        for (UUID participantId : request.participantIds()) {
            ReadStatus readStatus = new ReadStatus(participantId, channel.getId());
            readStatusRepository.saveReadStatus(readStatus);
        }

        return channel;
    }

    // 채널 공개 여부 별로 조건 달아준 전체 조회
    public List<ChannelFindResponse> findAllChannel(UUID userId) {
        List<Channel> channels = channelRepository.findAllChannels();

        return channels.stream()
                .filter(channel -> {
                    if (!channel.isLock()) {
                        return true;
                    } else {
                        List<UUID> participantIds = channel.getJoiningUsers();
                        return participantIds.contains(userId);
                    }
                })
                .map(channel -> {
                    // 최신 메시지 시간 조회
                    List<Message> findMessageByChannelId = messageRepository.findMessageByChannel(channel.getId());
                    Instant latestMessageTime = findMessageByChannelId.stream()
                            .map(Message::getCreatedAt)
                            .max(Comparator.naturalOrder())
                            .orElse(null);

                    if (latestMessageTime == null) {
                        System.out.println("메시지가 없습니다.");
                    }

                    List<UUID> participantIds = channel.getJoiningUsers();

                    return new ChannelFindResponse(
                            channel.getId(),
                            channel.getChannelName(),
                            latestMessageTime,
                            participantIds
                    );
                })
                .toList();
    }

    // 이름만으로 채널 조회
    @Override
    public List<Channel> getChannelUsingName(String channelName) {
        return channelRepository.findChannelUsingName(channelName);
    }

    // id로 채널 조회
    @Override
    public ChannelFindResponse getChannelUsingId(UUID channelId) {

        Channel channel = channelRepository.findChannelUsingId(channelId);
        if (!isChannelExist(channel)) {
            return null;
        }

        List<Message> findMessageByChannelId = messageRepository.findMessageByChannel(channelId);
        Instant latestMessageTime = findMessageByChannelId.stream()
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder())
                .orElse(null);


        if (!channel.isLock()) {
            return new ChannelFindResponse(
                    channel.getId(),
                    channel.getChannelName(),
                    latestMessageTime,
                    null
            );
        }
        else {
            return new ChannelFindResponse(
                    channel.getId(),
                    channel.getChannelName(),
                    latestMessageTime,
                    channel.getJoiningUsers()
            );
        }
    }

    // 채널 이름 수정
    @Override
    public boolean updateChannelName(ChannelUpdateNameRequest request) {
        Channel channel = channelRepository.findChannelUsingId(request.channelId());

        //채널 유효성 검사
        if (isChannelExist(channel)) {
            if (!channel.isLock()) {
                channelRepository.updateChannelName(request.channelId(), request.newName());
                System.out.println("채널 이름 수정이 완료되었습니다.");
                return true;
            }
            System.out.println("비공개 계정은 수정할 수 없습니다.");
            return false;
        }
        return false;
    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findChannelUsingId(channelId);

        if ((isChannelExist(channel)) && (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            List<Message> messages = messageRepository.findMessageByChannel(channelId);
            for (Message message : messages) {
                boolean result = messageRepository.deletedMessage(message.getMessageId());
                if (!result) {
                    System.out.println("삭제에 실패하였습니다");
                }
            }
            System.out.println("채널에 존재하는 메시지가 삭제되었습니다.");

            List<ReadStatus> readStatuses = readStatusRepository.findReadStatusByChannelId(channelId);
            for (ReadStatus readStatus : readStatuses) {
                boolean result = readStatusRepository.deleteReadStatusById(readStatus.getId());
                if (!result) {
                    System.out.println("삭제에 실패하였습니다");
                }
            }
            System.out.println("해당 채널의 ReadStatus가 삭제되었습니다.");

            channelRepository.deleteChannel(channelId);
            System.out.println("채널이 삭제되었습니다.");
            logger.info("채널이 삭제됨");

            return true;
        }
        return false;
    }

    // 채널에 유저 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel))&& (isChannelLock(channel, password))) {

            channelRepository.addUserToChannel(channelId, userId);
            System.out.println(channel.getChannelName() + "채널에" + userId + " 유저가 추가되었습니다");

            ReadStatus readStatus = new ReadStatus(userId, channelId);
            readStatusRepository.saveReadStatus(readStatus);

            return true;
        }

        return false;
    }

    // 참여한 유저 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {
        Channel channel = channelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            Optional<ReadStatus> readStatuses = readStatusRepository.findReadStatusByUserId(userId, channelId);
            if (readStatuses.isPresent()) {
                ReadStatus readStatus = readStatuses.get();
                boolean result = readStatusRepository.deleteReadStatusById(readStatus.getId());
                if (!result) {
                    System.out.println("삭제에 실패하였습니다");
                }
                System.out.println("해당 유저의 ReadStatus가 삭제되었습니다.");
            }

            channelRepository.deleteUserInChannel(channelId, userId);
            System.out.println(userId + " 유저가 삭제되었습니다");
            logger.info("채널의 유저가 삭제됨");

            return true;
        }
        return false;
    }

    // 채널 유효성 검사 - 존재여부
    private boolean isChannelExist(Channel channel){
        if(channel == null){
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 관리자 대조
    private boolean isChannelAdmin(Channel channel, UUID userId){
        if(!channel.getAdminId().equals(userId)){
            System.out.println("채널 정보 수정 권한이 없습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if(channel.isLock()){
            System.out.println("비공개 채널입니다. 비밀번호를 확인중입니다.");
            if(!channel.getPassword().equals(password)){
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }
}
