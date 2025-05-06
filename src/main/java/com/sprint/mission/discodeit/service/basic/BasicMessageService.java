package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
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
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;

    //메시지 생성
    @Override
    public Message CreateMessage(MessageCreateRequest request) {
        User sender = userRepository.findUserById(request.senderId());
        Channel channel = channelRepository.findChannelUsingId(request.channelId());

        if(isUserExist(sender) &&
                isChannelExist(channel) &&
                isParticipant(channel, request.senderId()) &&
                isChannelLock(channel, request.password())) {

            System.out.println("채널에 입장하셨습니다.");

            Message newMessage = new Message(request.channelId(), request.senderId(), request.messageContent());
            boolean created = messageRepository.createMessage(newMessage);

            if (!created) {
                throw new IllegalStateException("메시지 저장 실패");
            }

            // 첨부파일 저장
            if (request.binaryContent() != null) {
                for (byte[] data : request.binaryContent()){
                    BinaryContent content = new BinaryContent(request.senderId(), newMessage.getMessageId(), data);
                    boolean saved = binaryContentRepository.saveBinaryContent(content);
                    if(!saved){
                        logger.warning(newMessage.getMessageId() + " 의 첨부파일 저장 실패: ");
                    }

                    // 메시지에 첨부파일 id 연결
                    messageRepository.addAttachedFileId(newMessage.getMessageId(), content.getId());
                }
            }

            return newMessage;
        }
        return null;
    }

    // 채널 메시지 조회 (findAll 수정 버전)
    @Override
    public List<Message> findallByChannelId(UUID channelId, UUID userId, String password) {
        Channel channel = channelRepository.findChannelUsingId(channelId);
        User user = userRepository.findUserById(userId);

        if (isUserExist(user) && isChannelExist(channel)){
            if (channel.isLock()) {
                if (isCorrectPassword(channel, password)){
                    if (isParticipant(channel, userId)){
                        System.out.println("채널에 입장하셨습니다.");
                        return messageRepository.findMessageByChannel(channelId);
                    }
                }
            System.out.println("채널에 입장하셨습니다.");
            return messageRepository.findMessageByChannel(channelId);
            }
        }
        return null;
    }

    // 메시지 아이디 이용한 조회
    @Override
    public Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId) {
        Channel channel = channelRepository.findChannelUsingId(channelId);
        User user = userRepository.findUserById(userId);

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
        User user = userRepository.findUserById(senderId);

        List<Channel> foundChannel = channelRepository.findAllChannels().stream()
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

    // 메시지 수정
    @Override
    public boolean updateMessage(MessageUpdateRequest request) {
        Channel channel = channelRepository.findChannelUsingId(request.channelId());
        User user = userRepository.findUserById(request.senderId());

        Message message = messageRepository.findMessageById(request.messageId());

        if (isUserExist(user) && isChannelExist(channel) && isSender(message, request.senderId())){

            if (channel.isLock()) {
                if (isCorrectPassword(channel, request.password())) {
                    if (isParticipant(channel, request.senderId())) {

                        System.out.println("채널에 입장하셨습니다.");
                        messageRepository.updateMessage(request.messageId(), request.newMessageContent());

                        System.out.println("메시지가 수정되었습니다.");
                        logger.info("메시지 내용이 수정됨");
                        return true;
                    }
                }
            }
            System.out.println("채널에 입장하셨습니다.");
            messageRepository.updateMessage(request.messageId(), request.newMessageContent());

            System.out.println("메시지가 수정되었습니다.");
            logger.info("메시지 내용이 수정됨");
            return true;
        }
        return false;
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId, UUID senderId, String password) {
        Message message = messageRepository.findMessageById(messageId);

        User user = userRepository.findUserById(senderId);

        if (isMessageExist(message) &&
                isUserExist(user) &&
                isChannelExist(channelRepository.findChannelUsingId(message.getChannelId())) &&
                isChannelLock(channelRepository.findChannelUsingId(message.getChannelId()), password) &&
                isParticipant(channelRepository.findChannelUsingId(message.getChannelId()), senderId) &&
                isSender(message, senderId)){
            System.out.println("확인되었습니다.");


            List<UUID> relatedBinaryFile = message.getAttachedFileIds();

            // 연관 첨부파일 삭제
            if (relatedBinaryFile == null){
                System.out.println("첨부파일 없음.");
            }
            else {
                for (UUID binaryFileId : relatedBinaryFile) {
                    boolean deleteBinaryFile = binaryContentRepository.deleteById(binaryFileId);

                    if (deleteBinaryFile){
                        System.out.println("첨부파일이 삭제되었습니다.");
                    }
                    else{
                        logger.warning("첨부파일 삭제에 실패했습니다.");
                    }
                }
            }

            // 메시지 삭제
            boolean delete = messageRepository.deletedMessage(messageId);

            if (!delete) {
                throw new IllegalStateException("메시지 삭제 실패: " + messageId);
            }

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

    //채널 비밀번호 대조
    private boolean isCorrectPassword(Channel channel, String password){
        System.out.println("비공개 채널입니다. 비밀번호를 확인하고 있습니다..");
        if (!channel.getPassword().equals(password)) {
            System.out.println("비밀번호가 일치하지 않습니다.");
            return false;
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
            System.out.println("타인이 보낸 메시지입니다.");
            return false;
        }
        return true;
    }

}

