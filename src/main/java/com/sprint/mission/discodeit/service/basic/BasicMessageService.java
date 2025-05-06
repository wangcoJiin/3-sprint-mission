package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {

    private static final Logger logger = Logger.getLogger(BasicMessageService.class.getName()); // 필드로 Logger 선언

    private static final String FILE_PATH = "message.ser";

    // 의존성
    private final ChannelService channelService;
    private final UserService userService;
    private final MessageRepository messageRepository;

//    public BasicMessageService(UserService userService, ChannelService channelService, MessageRepository messageRepository) {
//        this.userService = userService;
//        this.channelService = channelService;
//        this.messageRepository = messageRepository;
//    }

    //메시지 생성
    @Override
    public Message CreateMessage(UUID channelId, String password, UUID senderId, String messageContent) {
        Channel channel = channelService.getChannelUsingId(channelId);
        User user = userService.getUserById(senderId);

        if(isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, senderId) &&
                isChannelLock(channel, password)) {

            System.out.println("채널에 입장하셨습니다.");

            Message newMessage = new Message(channelId, senderId, messageContent);
            messageRepository.createMessage(newMessage);

            System.out.println("메시지가 생성됐습니다.");

            return newMessage;
        }
        return null;
    }

    // 메시지 수정
    @Override
    public boolean updateMessage(UUID channelId, String password, UUID messageId, UUID senderId, String newMessageContent) {
        Channel channel = channelService.getChannelUsingId(channelId);
        User user = userService.getUserById(senderId);

        Message message = messageRepository.findMessageById(messageId);

        if (isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, senderId) &&
                isChannelLock(channel, password) &&
                isSender(message, senderId)) {
            System.out.println("채널에 입장하셨습니다.");

            messageRepository.updateMessage(messageId, newMessageContent);

            System.out.println("메시지가 수정되었습니다.");

            logger.info("메시지 내용이 수정됨");
            return true;
        }
        return false;
    }

    // 전체 메세지 조회
    @Override
    public List<Message> getAllMessage() {
        return messageRepository.findAllMessage();
    }

    // 채널 메시지 조회
    @Override
    public List<Message> getMessageByChannel(UUID channelId, UUID userId, String password) {
        Channel channel = channelService.getChannelUsingId(channelId);
        User user = userService.getUserById(userId);

        if (isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, userId) &&
                isChannelLock(channel, password)) {
            System.out.println("채널에 입장하셨습니다.");

            return messageRepository.findMessageByChannel(channelId);
        }
        return null;
    }

    // 메시지 아이디 이용한 조회
    @Override
    public Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId) {
        Channel channel = channelService.getChannelUsingId(channelId);
        User user = userService.getUserById(userId);

        Message message = messageRepository.findMessageById(messageId);


        if (isMessageExist(message) &&
                isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, userId) &&
                isChannelLock(channel, password)) {
            return message;

        }
        return null;
    }

    // 발송자를 이용해서 조회
    @Override
    public List<Message> userMessage(UUID senderId, String password) {
        User user = userService.getUserById(senderId);

        List<Channel> foundChannel = channelService.getAllChannels().stream()
                .filter(channel -> channel.getJoiningUsers().contains(senderId))
                .collect(Collectors.toList());

        for(Channel channel : foundChannel){
            if (isUserExist(user) &&
                    isChannelExist(channel) &&
                    isChannelLock(channel, password)) {
                System.out.println("확인되었습니다.");
            }
        }
        return messageRepository.userMessage(senderId);
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId, UUID senderId, String password) {
        Message message = messageRepository.findMessageById(messageId);

        User user = userService.getUserById(senderId);

        if (isMessageExist(message) &&
                isUserExist(user) &&
                isChannelExist(channelService.getChannelUsingId(message.getChannelId())) &&
                isParticipant(channelService.getChannelUsingId(message.getChannelId()), senderId) &&
                isChannelLock(channelService.getChannelUsingId(message.getChannelId()), password) &&
                isSender(message, senderId)){
            System.out.println("확인되었습니다.");

            messageRepository.deletedMessage(messageId);

            System.out.println("메시지가 삭제되었습니다.");
            logger.info("메시지 삭제됨");
            return true;
        }
        return false;
    }


    /* 자주 쓰는 조건문 정리 */

    //유저 존재 검사
    private boolean isUserExist(User user){
        if (user == null){
            System.out.println("해당하는 유저가 존재하지 않습니다.");
            return false;
        }
        return true;
    }

    //채널 존재 검사
    private boolean isChannelExist(Channel channel){
        if (channel == null){
            System.out.println("해당하는 채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }

    //참여자 여부 검사
    private boolean isParticipant(Channel channel, UUID senderId){
        if(!channel.getJoiningUsers().contains(senderId)){
            System.out.println("해당 채널에 참여하고 있지 않습니다.");
            return false;
        }
        return true;
    }

    //채널 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if (channel.isLock()) {
            System.out.println("비공개 채널입니다. 비밀번호를 확인하고 있습니다..");
            if (!channel.getPassword().equals(password)) {
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }

    //메시지 존재 검사
    private boolean isMessageExist(Message message){
        if(message == null){
            System.out.println("해당하는 메시지가 없습니다.");
            return false;
        }
        return true;
    }

    //메시지 발송자 대조
    private boolean isSender(Message message, UUID senderId){
        if (!message.getSenderId().equals(senderId)){
            System.out.println("본인이 보낸 메시지만 수정할 수 있습니다.");
            return false;
        }
        return true;
    }

}

