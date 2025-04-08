package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private Map<UUID, Message> messages = new HashMap<>();

    // 메시지 생성
    @Override
    public Message CreateMessage(UUID channelId, UUID senderId, String messageContent) {

        Message newMessage = new Message(channelId, senderId, messageContent);
        messages.put(newMessage.getMessageId(), newMessage);
        return newMessage;
    }

    // 메시지 수정
    @Override
    public boolean updateMessage(UUID messageId, UUID senderId, String newMessageContent) {
        Message message = messages.get(messageId);

        if (message !=null){
            if (message.getSenderId().equals(senderId)){
                message.updateMessageContent(newMessageContent);
                System.out.println("메시지 수정이 완료되었습니다.");
            }
            else{
                System.out.println("본인이 보낸 메시지만 수정할 수 있습니다.");
            }
        }
        else{
            System.out.println("해당하는 메시지가 없습니다.");
        }

        return false;
    }

    @Override
    public List<Message> getAllMessage() {
        return new ArrayList<>(messages.values());
    }

    // 채널에 있는 특정 메시지 조회
    @Override
    public Message getMessageById(UUID messageId, UUID channelId) {
        List<Message> messageInChannel = messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();

        for (Message message : messageInChannel){
            if (message.getMessageId() == messageId){
                return message;
            }
        }
        return null;
    }

    // 특정 채널의 메시지 조회
    @Override
    public List<Message> getMessageByChannel(UUID channelId) {
        return messages.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    // 유저의 메시지 조회
    @Override
    public List<Message> userMessage(UUID senderId) {
        return messages.values().stream()
                .filter(message -> message.getSenderId().equals(senderId))
                .collect(Collectors.toList());
    }

    // 메시지 삭제
    @Override
    public boolean deletedMessage(UUID messageId, UUID senderId) {
        Message message = messages.get(messageId);

        if(message != null){
            if (message.getSenderId().equals(senderId)){
                messages.remove(messageId);
                System.out.println("메시지가 삭제되었습니다.");
            }
            else{
                System.out.println("메시지를 보낸 사람만 삭제할 수 있습니다.");
            }
        }
        else{
            System.out.println("해당하는 메시지가 없습니다.");
        }
        return false;
    }
}