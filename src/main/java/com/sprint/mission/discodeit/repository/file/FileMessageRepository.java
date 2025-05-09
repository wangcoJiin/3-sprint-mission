package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import java.util.logging.Level;
import java.util.logging.Logger;


@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
@Repository
public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "messageRepository.ser";

    private static final Logger logger = Logger.getLogger(FileMessageRepository.class.getName());

    private final Map<UUID, Message> messages = new LinkedHashMap<>();


    // 메시지 정보를 파일로 저장
    private boolean saveMessageToFile(Map<UUID, Message> messages) {

        try (FileOutputStream fileOut = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fileOut)) {

            oos.writeObject(messages);
            return true;

        } catch (FileNotFoundException e) {
            // 파일 생성 실패 시 메시지
            logger.log(Level.SEVERE, FILE_PATH + "경로에 파일을 생성할 수 없습니다: ", e);
            return false;
        } catch (IOException e) {
            // 그 외 IO 예외
            logger.log(Level.SEVERE, "메시지 파일 저장 중 오류 발생", e);
            return false;
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
    public boolean saveMessage(Message message) {
        messages.put(message.getMessageId(), message);

        // 메시지 저장 상태 확인
        boolean success = saveMessageToFile(messages);
        if (!success) {
            logger.warning("메시지 저장에 실패했습니다: " + message.getMessageId());
        }
        return success;
    }

    // 메시지에 첨부파일 id 연결
    public boolean addAttachedFileId(UUID messageId, UUID attachedFileId) {
        Message message = findMessageById(messageId);

        message.getAttachedFileIds().add(attachedFileId);

        boolean success = saveMessageToFile(messages);

        if (!success) {
            logger.warning("채널 정보 저장에 실패했습니다: " + message.getMessageId());
        }
        return true;
    }

    // 메시지 내용 수정
    @Override
    public boolean updateMessage(UUID messageId, String newMessageContent) {
        Message message = findMessageById(messageId);
        message.updateMessageContent(newMessageContent);
        message.updateUpdatedAt(Instant.now());

        boolean success = saveMessageToFile(messages);
        if (!success) {
            logger.warning("메시지 저장에 실패했습니다: " + message.getMessageId());
        }
        return success;
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

        boolean success = saveMessageToFile(messages);
        if (!success) {
            logger.warning("메시지 저장에 실패했습니다: " + messages.get(messageId).getMessageId());
        }
        return success;
    }
}
