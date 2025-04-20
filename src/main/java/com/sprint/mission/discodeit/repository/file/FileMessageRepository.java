package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {

    private Map<UUID, Message> messages = new LinkedHashMap<>();

    private static final String FILE_PATH = "messageRepository.ser";

    // 메시지 정보를 파일로 저장
    private void saveMessageToFile(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 메시지 파일 읽어오기
    private Map<UUID, Message> loadMessageFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new LinkedHashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("메시지 파일 로드 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
    }

    // 메시지 생성
    @Override
    public boolean createMessage(Message message) {
        messages.put(message.getMessageId(), message);
        saveMessageToFile(messages);

        return true;
    }

    // 메시지 내용 수정
    @Override
    public boolean updateMessage(UUID messageId, String newMessageContent) {
        Message message = findMessageById(messageId);
        message.updateMessageContent(newMessageContent);
        message.updateUpdatedAt(System.currentTimeMillis());
        saveMessageToFile(messages);

        return true;
    }

    // 전체 메시지 조회
    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 아이디로 메시지 조회
    @Override
    public Message findMessageById(UUID messageId) {
        return messages.get(messageId);
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> findMessageByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 특정 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId) {
        return  messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId) {
        messages.remove(messageId);

        saveMessageToFile(messages);
        return true;
    }
}
