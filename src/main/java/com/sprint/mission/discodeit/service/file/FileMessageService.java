package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private final ChannelService fileChannelService;
    private final UserService fileUserService;

    private static final String FILE_PATH = "message.ser";

    // 생성자에서 의존성 주입
    public FileMessageService(UserService fileUserService, ChannelService fileChannelService) {
        this.fileUserService = fileUserService;
        this.fileChannelService = fileChannelService;
    }

    /* 동작 순서
     * 1. FileOutputStream(FILE_PATH) -> 파일을 write 모드(파일 쓰기 스트림)로 열기.
     * 2. ObjectOutputStream(oos) -> 객체 저장용 스트림으로 감싸기
     * 3. oos.writeObject(messages) -> Map<UUID, Message> 객체를 직렬화해서 파일에 기록
     * 4. IOException e -> 직렬화 중 IO 예외 발생 시 스택트레이스 출력 */

    // 메시지 맵을 파일에 저장 (파일에 메시지 객체 직렬화 하기)
    private void saveMessagesToFile(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 동작 순서
     * 1. FileInputStream(FILE_PATH) -> 파일을 바이너리 스트림으로 열기.
     * 2. ObjectInputStream (ois) -> 위에서 연 스트림을 객체 읽기용 스트림으로 감싸기
     * 3. ois.readObject() -> 파일에서 객체(Map<UUID, Message>)를 읽어들임 (직렬화된 데이터를 자바 객체로 역직렬화 함)
     * 4. (Map<UUID, Channel>) -> object로 반환되므로 Map으로 타입캐스팅 해줌
     * 5. FileNotFoundException e -> 최초 실행시에 파일 없을 수도 있음 -> 빈 해시맵 반환
     * 6. IOException | ClassNotFoundException e -> 역직렬화 실패 시 스택트레이스 출력하고 빈 해시맵 반환 */

    // 파일에서 메시지 맵 불러오기
    private Map<UUID, Message> loadMessagesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            //err 사용하면 표준 에러 스트림으로 출력되고 콘솔에서 빨갛게 강조됨
            System.err.println("파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    //메시지 생성
    @Override
    public Message CreateMessage(UUID channelId, String password, UUID senderId, String messageContent) {
        Map<UUID, Message> messages = loadMessagesFromFile();
        Channel channel = fileChannelService.getChannelUsingId(channelId);
        User user = fileUserService.getUserById(senderId);

        if(isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, senderId) &&
                isChannelLock(channel, password)) {

            System.out.println("채널에 입장하셨습니다.");
            Message newMessage = new Message(channelId, senderId, messageContent);
            messages.put(newMessage.getMessageId(), newMessage);
            saveMessagesToFile(messages);
            System.out.println("메시지가 생성됐습니다.");

            return newMessage;
        }
        return null;
    }

    // 메시지 수정
    @Override
    public boolean updateMessage(UUID channelId, String password, UUID messageId, UUID senderId, String newMessageContent) {
        Map<UUID, Message> messages = loadMessagesFromFile();
        Channel channel = fileChannelService.getChannelUsingId(channelId);
        User user = fileUserService.getUserById(senderId);
        Message message = messages.get(messageId);

        if (isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, senderId) &&
                isChannelLock(channel, password) &&
                isSender(message, senderId)) {
            System.out.println("채널에 입장하셨습니다.");
            message.updateMessageContent(newMessageContent);
            message.updateUpdatedAt(System.currentTimeMillis());
            System.out.println("메시지가 수정되었습니다.");
            saveMessagesToFile(messages);
            return true;
        }
        return false;
    }

    // 전체 메세지 조회
    @Override
    public List<Message> getAllMessage() {
        Map<UUID, Message> messages = loadMessagesFromFile();
        return new ArrayList<>(messages.values());
    }

    // 채널 메시지 조회
    @Override
    public List<Message> getMessageByChannel(UUID channelId, UUID userId, String password) {
        Map<UUID, Message> messages = loadMessagesFromFile();
        Channel channel = fileChannelService.getChannelUsingId(channelId);
        User user = fileUserService.getUserById(userId);

        if (isUserExist(user) &&
                isChannelExist(channel) &&
                isParticipant(channel, userId) &&
                isChannelLock(channel, password)) {
            System.out.println("채널에 입장하셨습니다.");
            return messages.values().stream()
                    .filter(message -> message.getChannelId().equals(channelId))
                    .collect(Collectors.toList());
        }
        return null;
    }

    // 메시지 아이디 이용한 조회
    @Override
    public Message getMessageById(UUID channelId, UUID userId, String password, UUID messageId) {
        Map<UUID, Message> messages = loadMessagesFromFile();
        Channel channel = fileChannelService.getChannelUsingId(channelId);
        User user = fileUserService.getUserById(userId);
        Message message = messages.get(messageId);

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
        Map<UUID, Message> messages = loadMessagesFromFile();
        User user = fileUserService.getUserById(senderId);
       List<Channel> foundChannel = fileChannelService.getAllChannels().stream()
               .filter(channel -> channel.getJoiningUsers().contains(senderId))
               .collect(Collectors.toList());

       for(Channel channel : foundChannel){
           if (isUserExist(user) &&
                   isChannelExist(channel) &&
                   isChannelLock(channel, password)) {
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
        Map<UUID, Message> messages = loadMessagesFromFile();
        Message message = messages.get(messageId);
        User user = fileUserService.getUserById(senderId);

        if (isMessageExist(message) &&
                isUserExist(user) &&
                isChannelExist(fileChannelService.getChannelUsingId(message.getChannelId())) &&
                isParticipant(fileChannelService.getChannelUsingId(message.getChannelId()), senderId) &&
                isChannelLock(fileChannelService.getChannelUsingId(message.getChannelId()), password) &&
                isSender(message, senderId)){
            System.out.println("확인되었습니다.");
            messages.remove(messageId);
            saveMessagesToFile(messages);
            System.out.println("메시지가 삭제되었습니다.");
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
