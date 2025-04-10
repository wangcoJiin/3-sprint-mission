package com.sprint.mission.discodeit.run;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class Application {
    public static void main(String[] args) {

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(channelService, userService);

        //유저 테스트
        List<User> users = createUserManagement(userService); // 유저 관리
        List<Channel> channels = createChannelManagement(channelService, users); // 채널 관리
        createMessageManagement(messageService, users, channels); // 메시지 관리
    }

    //유저 관리 메서드
    private static List<User> createUserManagement(UserService userService) {

        System.out.println("\n==============================유저 기능 테스트==============================");

        User newUser1 = userService.createUser("홍길동");
        User newUser2 = userService.createUser("조현지");
        User newUser3 = userService.createUser("백은호");
        User newUser4 = userService.createUser("정윤지");
        User newUser5 = userService.createUser("김이박");
        User newUser6 = userService.createUser("이주용");

        System.out.println("\n유저가 생성되었습니다.");

        // 전체 유저 정보 조회 (다건 조회)
        System.out.println("\n전체 사용자 목록:");
        List<User> allUsers = userService.getAllUsers();
        for (User user : allUsers) {
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 정보 수정 시점: " + user.getUpdatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        // 유저 아이디 이용해서 조회 (단건 조회)
        User foundUser = userService.getUserById(newUser1.getId());
        System.out.println("\nID로 조회된 사용자:");
        System.out.println("ID: " + foundUser.getId() + ", 이름: " + foundUser.getName() + ", 유저 생성 시점: " + foundUser.getCreatedAt() + ", 정보 수정 시점: " + foundUser.getUpdatedAt() + ", 유저 활동 상태: " + foundUser.getConnectState());


        // 유저 이름 이용해서 조회
        System.out.println("\n사용자 검색 ('홍길동'):");
        List<User> foundByName = userService.searchUsersByName("홍길동");
        for (User user : foundByName) {
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 정보 수정 시점: " + user.getUpdatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        //유저 이름 변경(홍길동 -> 강길동)
        System.out.println("\n이름 수정 중입니다:");
        boolean updateName = userService.updateUserName(newUser1.getId(), "강길동");
        if (updateName) {
            System.out.println("이름 수정이 완료되었습니다");
        } else {
            System.out.println("이름 수정이 실패하였습니다");
        }

        // 유저 활동상태 변경 (초기상태 -> 온라인)
        System.out.println("\n활동 상태 수정 중입니다:");
        boolean updateResult = userService.updateConnectState("강길동", "온라인");
        if (updateResult) {
            System.out.println("상태 수정이 완료되었습니다");
        } else {
            System.out.println("상태 수정이 실패하였습니다");
        }

        // 유저 이름으로 수정 결과 확인
        System.out.println("\n수정된 상태를 확인해보세요(홍길동 -> 강길동 / 초기상태 -> 온라인):");
        List<User> updateUsers = userService.searchUsersByName("강길동");
        for (User updateuser : updateUsers) {
            System.out.println("ID: " + updateuser.getId() + ", 이름: " + updateuser.getName() + ", 유저 생성 시점: " + updateuser.getCreatedAt() + ", 정보 수정 시점: " + updateuser.getUpdatedAt() + ", 유저 활동 상태: " + updateuser.getConnectState());
        }

        // 유저 id 이용해서 삭제
        System.out.println("\n사용자 삭제 newUser5:");
        userService.deleteUserById(newUser5.getId());

        // 최종 유저 목록 조회
        System.out.println("\n최종 사용자 목록:");
        List<User> totalUsers = userService.getAllUsers();
        for (User user : totalUsers) {
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 정보 수정 시점: " + user.getUpdatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        return totalUsers;
    }

    // 채널 관리 메서드
    private static List<Channel> createChannelManagement(ChannelService channelService, List<User> users) {


        System.out.println("\n==============================채널 기능 테스트==============================\n");

        // 채널 생성
        Channel newChannel1 = channelService.createChannel("first Channel", users.get(0).getId(), false, "");
        Channel newChannel2 = channelService.createChannel("second Channel", users.get(1).getId(), true, "1111");
        Channel newChannel3 = channelService.createChannel("third Channel", users.get(3).getId(), true, "0000");
        Channel newChannel4 = channelService.createChannel("fourth Channel", users.get(4).getId(), false, "");
        System.out.println("\n채널이 생성되었습니다.");


        // 전체 채널 조회 (다건 조회)
        System.out.println("\n전체 채널 목록 조회:");
        List<Channel> allChannel = channelService.getAllChannels();
        for (Channel channel : allChannel) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        // 특정 채널 조회 (단건 조회)
        System.out.println("\n특정 채널 조회: ");
        Channel oneChannel = channelService.getChannelUsingId(newChannel1.getId());
        System.out.println("ID: " + oneChannel.getId() + ", 채널명: " + oneChannel.getChannelName() + ", 비공개 채널: " + oneChannel.isLock());


        // 채널 이름 수정 (방장의 경우)
        System.out.println("\n채널 이름 수정 (방장):");
        channelService.updateChannelName(newChannel1.getId(), users.get(0).getId(), "", "my first Channel");


        // 채널 이름 수정 (방장이 아닐 경우)
        System.out.println("\n채널 이름 수정 (방장이 아닌 경우):");
        channelService.updateChannelName(newChannel1.getId(), users.get(3).getId(), "", "edited: my first Channel");


        // 수정된 상태 확인을 위한 조회
        System.out.println("\n변경된 채널 이름을 확인해보세요:");
        List<Channel> modifyChannelName = channelService.getAllChannels();
        for (Channel channel : modifyChannelName) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }


        // 비공개 채널 이름 수정
        System.out.println("\n비공개 채널 이름 수정 (비밀번호 일치):");
        channelService.updateChannelName(newChannel2.getId(), users.get(1).getId(), "1111", "my second Channel");


        // 비밀번호가 다를 경우
        System.out.println("\n비공개 채널 이름 수정 (비밀번호 불일치):");
        channelService.updateChannelName(newChannel2.getId(), users.get(1).getId(), "2222", "my second Channel");


        // 수정된 상태 확인을 위한 조회
        System.out.println("\n변경된 채널 이름을 확인해보세요:");
        List<Channel> modifyChannelName2 = channelService.getAllChannels();
        for (Channel channel : modifyChannelName2) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        System.out.println("\n채널에 유저 추가:");
        channelService.addUserToChannel(newChannel1.getId(), users.get(3).getId(), "");
        channelService.addUserToChannel(newChannel1.getId(), users.get(4).getId(), "");

        channelService.addUserToChannel(newChannel3.getId(), users.get(0).getId(), "0000");
        channelService.addUserToChannel(newChannel3.getId(), users.get(1).getId(), "0000");
        channelService.addUserToChannel(newChannel3.getId(), users.get(2).getId(), "0000");


        // 채널에 추가된 유저 확인
        System.out.println("\n채널에 추가된 유저 id 확인: ");
        System.out.println("newChannel1의 유저: " + newChannel1.getJoiningUsers());
        System.out.println("newChannel3의 유저: " + newChannel3.getJoiningUsers());


        // 채널 삭제
        System.out.println("\n채널 삭제: ");
        channelService.deleteChannel(newChannel2.getId(), "my second Channel", users.get(1).getId(), "1111");


        // 채널 삭제 확인을 위한 조회
        System.out.println("\n채녈이 삭제된 결과를 확인해보세요: ");
        List<Channel> deleteChannel = channelService.getAllChannels();
        for (Channel channel : deleteChannel) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        return deleteChannel;
    }

    // 메시지 관리 메서드
    private static void createMessageManagement(MessageService messageService, List<User> users, List<Channel> channels) {

        System.out.println("\n==============================메시지 기능 테스트==============================");

        // 메시지 생성
        System.out.println("\n메시지 생성: ");
        Message channel3_newMessage1 = messageService.CreateMessage(channels.get(1).getId(), "0000", users.get(3).getId(), "User3: new Message Create!");
        Message channel3_newMessage2 = messageService.CreateMessage(channels.get(1).getId(), "0000", users.get(1).getId(), "User1: HEllO!");
        Message channel3_newMessage3 = messageService.CreateMessage(channels.get(1).getId(), "0000", users.get(0).getId(), "User0: heyyyy~");
        Message channel3_newMessage4 = messageService.CreateMessage(channels.get(1).getId(), "0000", users.get(0).getId(), "User0: how are you?");
        Message channel1_newMessage1 = messageService.CreateMessage(channels.get(0).getId(), "", users.get(3).getId(), "User3: channel1's first message!");
        Message channel1_newMessage2 = messageService.CreateMessage(channels.get(0).getId(), "", users.get(4).getId(), "User4: my message!");


        // 비밀번호가 틀린 경우
        Message channel3_newMessage5 = messageService.CreateMessage(channels.get(1).getId(), "1111", users.get(0).getId(), "User0: hey");


        // 전체 메시지 조회 (다건 조회)
        System.out.println("\n전체 메시지 조회: ");
        List<Message> allMessage = messageService.getAllMessage();
        for (Message message : allMessage) {
            System.out.println("채널 Id: " + message.getChannelId() + ", 보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        //채널 3의 특정 메시지 조회 (단건 조회)
        System.out.println("\n특정 채널의 특정 메시지 조회: ");
        Message messageInChannel = messageService.getMessageById(channels.get(1).getId(), users.get(0).getId(), "0000", channel3_newMessage2.getMessageId());
        System.out.println("보낸 사람: " + messageInChannel.getSenderId() + ", 메시지 내용: " + messageInChannel.getMessageContent());


        // 특정 유저가 보낸 메시지 조회
        System.out.println("\n특정 유저가 보낸 메시지 조회: ");
        List<Message> userMessage = messageService.userMessage(users.get(0).getId(), "0000");
        for(Message message : userMessage){
            System.out.println("보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        //메시지 수정(본인의 메시지)
        System.out.println("\n메시지 수정: ");
        messageService.updateMessage(channels.get(0).getId(), "", channel1_newMessage2.getMessageId(), users.get(4).getId(), "User4: modify message!");


        //메시지 수정(타인의 메시지)
        System.out.println("\n메시지 수정: ");
        messageService.updateMessage(channels.get(0).getId(), "", channel1_newMessage2.getMessageId(), users.get(3).getId(), "User3: Can I edit your message?");


        //수정된 결과 확인을 위한 해당 채널 메시지 조회
        System.out.println("\n수정된 결과 확인: ");
        List<Message> modifyMessage = messageService.getMessageByChannel(channels.get(0).getId(), users.get(3).getId(), "");
        for (Message message : modifyMessage) {
            System.out.println("보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        // 메시지 삭제(본인의 메시지)
        System.out.println("\n메시지 삭제: ");
        messageService.deletedMessage(channel3_newMessage2.getMessageId(), users.get(1).getId(), "0000");


        //메시지 삭제(타인의 메시지)
        System.out.println("\n메시지 삭제: ");
        messageService.deletedMessage(channel3_newMessage1.getMessageId(), users.get(0).getId(), "0000");


        //삭제된 결과 확인을 위한 조회
        System.out.println("\n삭제된 결과 확인: ");
        List<Message> deleteMessageTest = messageService.getAllMessage();
        for (Message message : deleteMessageTest) {
            System.out.println("채널 Id: " + message.getChannelId() + ", 보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

    }

}












