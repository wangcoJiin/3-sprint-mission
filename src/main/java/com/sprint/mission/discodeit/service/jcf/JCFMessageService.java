package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private Map<UUID, Message> messages = new LinkedHashMap<>();

    private ChannelService channelService;
    private UserService userService;

    // 생성자에서 의존성 주입
    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    // 메시지 생성
    @Override
    public Message CreateMessage(UUID channelId, String password, UUID senderId, String messageContent) {

        if(channelService.getChannelUsingId(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return null;
        }

        if(!channelService.getChannelUsingId(channelId).getJoiningUsers().contains(senderId)) {
            System.out.println("참여중인 채널이 아닙니다.");
            return null;
        }

        if (channelService.getChannelUsingId(channelId).isLock()) {
            System.out.println("비공개 채널입니다.");

            if (!Objects.equals(channelService.getChannelUsingId(channelId).getPassword(), password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return null;
            }

            System.out.println("비밀번호 확인에 성공했습니다.");
        }

        Message newMessage = new Message(channelId, senderId, messageContent);
        messages.put(newMessage.getMessageId(), newMessage);
        System.out.println("메시지가 추가되었습니다.");
        return newMessage;
    }

    // 메시지 수정
    @Override
    public boolean updateMessage(UUID channelId, String password, UUID messageId, UUID senderId, String newMessageContent) {
        Message message = messages.get(messageId);

        if (channelService.getChannelUsingId(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }

        if (message == null) {
            System.out.println("해당하는 메시지가 없습니다.");
            return false;
        }

        if (message.getChannelId() != channelId) {
            System.out.println("해당 채널의 메시지가 아닙니다.");
            return false;
        }

        if (channelService.getChannelUsingId(channelId).isLock()) {
            System.out.println("비공개 채널입니다.");

            if (!Objects.equals(channelService.getChannelUsingId(channelId).getPassword(), password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }

            System.out.println("비밀번호 확인에 성공했습니다.");
            System.out.println('\'' + channelService.getChannelUsingId(channelId).getChannelName() + '\'' + "에 접속했습니다.");
        }

        if (!message.getSenderId().equals(senderId)) {
            System.out.println("본인이 보낸 메시지만 수정할 수 있습니다.");
            return false;
        }

        message.updateMessageContent(newMessageContent);
        System.out.println("메시지 수정이 완료되었습니다.");
        return true;
    }

    // 모든 메시지 조회
    @Override
    public List<Message> getAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 채널에 있는 특정 메시지 조회
    @Override
    public Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId) {
        Message message = messages.get(messageId);

        if(channelService.getChannelUsingId(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return null;
        }

        if(message == null) {
            System.out.println("해당하는 메시지가 없습니다.");
            return null;
        }

        if (channelService.getChannelUsingId(channelId).isLock()) {
            System.out.println("비공개 채널입니다.");

            if (!channelService.getChannelUsingId(channelId).getJoiningUsers().contains(userId)) {
                System.out.println("참여중인 채널이 아닙니다.");
                return null;
            }

            System.out.println("비밀번호 확인중입니다...");
            if (!Objects.equals(channelService.getChannelUsingId(channelId).getPassword(), password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return null;
            }
        }
        return message;
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> getMessageByChannel(UUID channelId, UUID userId, String password) {

        if(channelService.getChannelUsingId(channelId) == null) {
            System.out.println("채널이 존재하지 않습니다.");
            return null;
        }
        if(channelService.getChannelUsingId(channelId).isLock()) {
            System.out.println("비공개 채널입니다.");

            if (!channelService.getChannelUsingId(channelId).getJoiningUsers().contains(userId)) {
                System.out.println("참여중인 채널이 아닙니다.");
                return null;
            }
            System.out.println("비밀번호 확인중입니다...");
            if (!Objects.equals(channelService.getChannelUsingId(channelId).getPassword(), password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return null;
            }
            System.out.println("확인되었습니다.");
        }

        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId, String password) {

        List<Channel> foundChannelByParticipate = channelService.getAllChannels().stream()
                .filter(channel -> channel.getJoiningUsers().contains(senderId))
                .collect(Collectors.toList());

        for (Channel channel : foundChannelByParticipate){

            if(channel.isLock()) {
                System.out.println("비공개 채널입니다.");
                System.out.println("비밀번호 확인중입니다...");

                if (!Objects.equals(channel.getPassword(), password)) {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                    return null;
                }

                System.out.println("확인되었습니다.");
            }

        }
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId, UUID senderId, String password) {
        Message message = messages.get(messageId);

        if(message == null) {
            System.out.println("해당하는 메시지가 없습니다.");
            return false;
        }

        if (channelService.getChannelUsingId(message.getChannelId()).isLock()) {
            System.out.println("비공개 채널의 메시지 입니다.");
            System.out.println("비밀번호 확인중입니다...");

            if (!Objects.equals(channelService.getChannelUsingId(message.getChannelId()).getPassword(), password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
            System.out.println("확인되었습니다.");

            if (!message.getSenderId().equals(senderId)) {
                System.out.println("본인의 메시지만 삭제할 수 있습니다.");
                return false;
            }
        }

        messages.remove(messageId);
        System.out.println("메시지가 삭제되었습니다.");
        return true;
    }
}