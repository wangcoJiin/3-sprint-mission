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

import java.util.List;
import java.util.UUID;

public class Application {
    public static void main(String[] args) {

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService();
        MessageService messageService = new JCFMessageService(channelService);

        System.out.println("==============================유저 기능 테스트==============================");

        // 유저 생성
        User newUser1 = userService.createUser("홍길동");
        User newUser2 = userService.createUser("정길순");
        User newUser3 = userService.createUser("이길석");
        User newUser4 = userService.createUser("김민석");
        User newUser5 = userService.createUser("김이박");

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
        System.out.println("\n사용자 삭제 newUser2:");
        userService.deleteUserById(newUser2.getId());

        // 최종 유저 목록 조회
        System.out.println("\n최종 사용자 목록:");
        List<User> totalUsers = userService.getAllUsers();
        for (User user : totalUsers) {
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 정보 수정 시점: " + user.getUpdatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        System.out.println("\n==============================채널 기능 테스트==============================");

        // 채널 생성
        Channel newChannel1 = channelService.createChannel("first Channel", newUser1.getId(), false, "");
        Channel newChannel2 = channelService.createChannel("second Channel", newUser3.getId(), true, "1111");
        Channel newChannel3 = channelService.createChannel("third Channel", newUser1.getId(), true, "0000");
        Channel newChannel4 = channelService.createChannel("fourth Channel", newUser5.getId(), false, "");

        System.out.println("\n채널이 생성되었습니다.");

        // 전체 채널 조회 (다건 조회)
        System.out.println("\n전체 채널 목록 조회:");
        List<Channel> allChannel = channelService.getAllChannels();
        for (Channel channel : allChannel) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        // 특정 채널 조회 (단건 조회)
        System.out.println("\n특정 채널 조회: ");
        Channel oneChannel = channelService.getChannelUsingId(newChannel3.getId());
        System.out.println("ID: " + oneChannel.getId() + ", 채널명: " + oneChannel.getChannelName() + ", 비공개 채널: " + oneChannel.isLock());


        // 채널 이름 수정 (방장의 경우)
        System.out.println("\n채널 이름 수정 (방장):");
        channelService.updateChannelName(newChannel1.getId(), "first Channel", newUser1.getId(), "", "이름이 수정된 채널");


        // 채널 이름 수정 (방장이 아닐 경우)
        System.out.println("\n채널 이름 수정 (방장이 아닌 경우):");
        channelService.updateChannelName(newChannel1.getId(), "first Channel", newUser3.getId(), "", "이름이 수정된 채널");


        // 수정된 상태 확인을 위한 조회
        System.out.println("\n변경된 채널 이름을 확인해보세요:");
        List<Channel> modifyChannelName = channelService.getAllChannels();
        for (Channel channel : modifyChannelName) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }


        // 비공개 채널 이름 수정
        System.out.println("\n비공개 채널 이름 수정 (비밀번호 일치):");
        channelService.updateChannelName(newChannel2.getId(), "second Channel", newUser3.getId(), "1111", "이름이 수정된 두번째 채널");


        // 비밀번호가 다를 경우
        System.out.println("\n비공개 채널 이름 수정 (비밀번호 불일치):");
        channelService.updateChannelName(newChannel2.getId(), "second Channel", newUser3.getId(), "2222", "이름이 수정된 두번째 채널");


        // 수정된 상태 확인을 위한 조회
        System.out.println("\n변경된 채널 이름을 확인해보세요:");
        List<Channel> modifyChannelName2 = channelService.getAllChannels();
        for (Channel channel : modifyChannelName2) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        System.out.println("\n채널에 유저 추가:");
        channelService.addUserToChannel(newChannel1.getId(), newUser4.getId(), "");
        channelService.addUserToChannel(newChannel1.getId(), newUser5.getId(), "");

        channelService.addUserToChannel(newChannel3.getId(), newUser3.getId(), "0000");
        channelService.addUserToChannel(newChannel3.getId(), newUser4.getId(), "0000");
        channelService.addUserToChannel(newChannel3.getId(), newUser5.getId(), "0000");


        // 채널에 추가된 유저 확인
        System.out.println("\n채널에 추가된 유저 id 확인: ");
        System.out.println("newChannel1의 유저: " + newChannel1.getJoiningUsers());
        System.out.println("newChannel3의 유저: " + newChannel3.getJoiningUsers());


        // 채널 삭제
        System.out.println("\n채널 삭제: ");
        channelService.deleteChannel(newChannel2.getId(), "이름이 수정된 두번째 채널", newUser3.getId(), "1111");


        // 채널 삭제 확인을 위한 조회
        System.out.println("\n채녈이 삭제된 결과를 확인해보세요: ");
        List<Channel> deleteChannel = channelService.getAllChannels();
        for (Channel channel : deleteChannel) {
            System.out.println("ID: " + channel.getId() + ", 채널명: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }


        System.out.println("\n==============================메시지 기능 테스트==============================");

//        // 메시지 생성
//        System.out.println("\n메시지 생성: ");
//        Message channel3_newMessage1 = messageService.CreateMessage(newChannel3.getId(), newUser3.getId(), "newUser3: new Message Create!");
//        Message channel3_newMessage2 = messageService.CreateMessage(newChannel3.getId(), newUser3.getId(), "newUser3: HEllO!");
//        Message channel3_newMessage3 = messageService.CreateMessage(newChannel3.getId(), newUser1.getId(), "newUser1: heyyyy~");
//        Message channel1_newMessage1 = messageService.CreateMessage(newChannel1.getId(), newUser4.getId(), "newUser4: channel1's first message!");
//        Message channel1_newMessage2 = messageService.CreateMessage(newChannel1.getId(), newUser5.getId(), "newUser5: my message!");
//        System.out.println("메시지가 추가가 완료되었습니다.");

        // 메시지 생성
        System.out.println("\n메시지 생성: ");
        Message channel3_newMessage1 = messageService.CreateMessage(newChannel3.getId(), "0000",  newUser3.getId(), "newUser3: new Message Create!");
        Message channel3_newMessage2 = messageService.CreateMessage(newChannel3.getId(), "0000", newUser3.getId(), "newUser3: HEllO!");
        Message channel3_newMessage3 = messageService.CreateMessage(newChannel3.getId(), "0000", newUser5.getId(), "newUser5: heyyyy~");
        Message channel1_newMessage1 = messageService.CreateMessage(newChannel1.getId(), "", newUser4.getId(), "newUser4: channel1's first message!");
        Message channel1_newMessage2 = messageService.CreateMessage(newChannel1.getId(), "", newUser5.getId(), "newUser5: my message!");

        // 비밀번호가 틀린 경우
        Message channel3_newMessage4 = messageService.CreateMessage(newChannel3.getId(), "1111", newUser4.getId(), "newUser4: hey");


        // 전체 메시지 조회 (다건 조회)
        System.out.println("\n전체 메시지 조회: ");
        List<Message> allMessage = messageService.getAllMessage();
        for (Message message : allMessage){
            System.out.println("채널 Id: " + message.getChannelId() + ", 보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        //채널 3의 특정 메시지 조회 (단건 조회)
        System.out.println("\n특정 채널의 특정 메시지 조회: ");
        Message messageInChannel = messageService.getMessageById(channel3_newMessage2.getMessageId(), newChannel3.getId());
        System.out.println("보낸 사람: " + messageInChannel.getSenderId() + ", 메시지 내용: " + messageInChannel.getMessageContent());


        //메시지 수정(본인의 메시지)
        System.out.println("\n메시지 수정: ");
        messageService.updateMessage(channel1_newMessage2.getMessageId(), newUser5.getId(), "newUser5: modify message!");


        //메시지 수정(타인의 메시지)
        System.out.println("\n메시지 수정: ");
        messageService.updateMessage(channel1_newMessage2.getMessageId(), newUser4.getId(), "newUser4: Can I edit your message?");


        //수정된 결과 확인을 위한 조회
        System.out.println("\n수정된 결과 확인: ");
        List<Message> modifyMessage = messageService.getMessageByChannel(newChannel1.getId());
        for (Message message : modifyMessage){
            System.out.println("보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        // 메시지 삭제(본인의 메시지)
        System.out.println("\n메시지 삭제: ");
        messageService.deletedMessage(channel3_newMessage1.getMessageId(), newUser3.getId());


        //메시지 삭제(타인의 메시지)
        System.out.println("\n메시지 삭제: ");
        messageService.deletedMessage(channel1_newMessage1.getMessageId(), newUser3.getId());


        //삭제된 결과 확인을 위한 조회
        System.out.println("\n삭제된 결과 확인: ");
        List<Message> deleteMessageTest = messageService.getAllMessage();
        for (Message message : deleteMessageTest){
            System.out.println("채널 Id: " + message.getChannelId() + ", 보낸 사람: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }
    }
}
