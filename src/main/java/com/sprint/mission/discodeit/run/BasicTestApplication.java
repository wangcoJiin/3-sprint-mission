package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class BasicTestApplication {
    public static void main(String[] args) {

        deleteFileIfExists("userRepository.ser");
        deleteFileIfExists("channelRepository.ser");
        deleteFileIfExists("messageRepository.ser");

        Scanner scanner = new Scanner(System.in);

        // JCF와 File 기반 방식 중 선택
        System.out.println("사용할 저장 방식을 선택하세요:");
        System.out.println("1 => 파일 기반");
        System.out.println("2 => JCF 메모리 기반");
        System.out.print("원하는 번호 입력: ");
        int option = scanner.nextInt();

        UserRepository userRepository;
        ChannelRepository channelRepository;
        MessageRepository messageRepository;

        if (option == 1) {
            // 파일 저장 로직
            userRepository = new FileUserRepository();
            channelRepository = new FileChannelRepository();
            messageRepository = new FileMessageRepository();

            UserService userService = new BasicUserService(userRepository);
            ChannelService channelService = new BasicChannelService(channelRepository);
            MessageService messageService = new BasicMessageService(userService, channelService, messageRepository);

            System.out.println("파일 저장소 선택됨");
            List<User> fileUser = UserManagementTest(userService); // 유저 관리
            List<Channel> fileChannel = channelManagementTest(channelService, fileUser); // 채널 관리
            messageManagementTest(messageService, fileUser, fileChannel); // 메시지 관리
            scanner.close();

        } else {
            // 메모리 저장 로직
            userRepository = new JCFUserRepository();
            channelRepository = new JCFChannelRepository();
            messageRepository = new JCFMessageRepository();
            
            UserService userService = new BasicUserService(userRepository);
            ChannelService channelService = new BasicChannelService(channelRepository);
            MessageService messageService = new BasicMessageService(userService, channelService, messageRepository);
            
            System.out.println("JCF 저장소 선택됨");
            List<User> jcfUser = UserManagementTest(userService); // 유저 관리
            List<Channel> jcfChannel = channelManagementTest(channelService, jcfUser); // 채널 관리
            messageManagementTest(messageService, jcfUser, jcfChannel); // 메시지 관리
            scanner.close();

        }

    }


    // 유저 서비스 테스트
    private static List<User> UserManagementTest(UserService userService){

        try {
            System.out.println("\n" +
                    "=================================================================================\n" +
                    " ###  ##   ####    ######   #####             ######   ######    ####     #####  \n" +
                    " ##   ##  #    #   #        #    #            # ## #   #        #    #      #    \n" +
                    " ##   ##  #        #        #    #              ##     #        #           #    \n" +
                    " ##   ##   ####    ####     #####               ##     ####      ####       #    \n" +
                    " ##   ##       #   #        #  #                ##     #             #      #    \n" +
                    " ### ##   #    #   #        #   #               ##     #        #    #      #    \n" +
                    "  ####     ####    ######   #    #             ####    ######    ####       #    \n" +
                    "=================================================================================");
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            System.err.println("대기 중 인터럽트가 발생했습니다.: " + e.getMessage());
        }

        // 유저생성
        System.out.println("\n=== 유저 생성 ===");
        User basicUser1 = userService.createUser("홍길동");
        User basicUser2 = userService.createUser("김동명이인");
        User basicUser3 = userService.createUser("강이름수정");
        User basicUser4 = userService.createUser("박동명이인");
        User basicUser5 = userService.createUser("김철수");

        // 전체 유저 조회
        System.out.println("\n=== 전체 유저 조회 ===");
        List<User> firstAllUsers = userService.getAllUsers();

        // 아이디로 유저 조회
        System.out.println("\n=== 아이디로 유저 조회 ===");
        System.out.println(userService.getUserById(basicUser1.getId()));

        //이름으로 유저 조회
        System.out.println("\n=== 이름으로 유저 조회 ===");
        List<User> foundTestByName = userService.searchUsersByName("김동명이인");
        for(User user: foundTestByName){
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        // 유저 이름, 활동상태 둘 다 변경
        System.out.println("\n=== 유저 이름과 활동상태 변경 ===");
        userService.updateUser(basicUser2.getId(), "박동명이인", "온라인");

        //유저 이름 변경
        System.out.println("\n=== 유저 이름 변경 ===");
        userService.updateUserName(basicUser3.getId(), "강이름");

        // 전체 유저 조회
        System.out.println("\n=== 변경된 결과 확인 ===");
        List<User> testAllUser2 = userService.getAllUsers();
        for(User user : testAllUser2){
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        // 유저 활동상태 변경 (중복 이름)
        System.out.println("\n=== 유저 활동상태 변경 (동명이인 선택 시) ===");
        userService.updateConnectState("박동명이인", "오프라인");

        // 전체 유저 조회
        System.out.println("\n=== 변경된 결과 확인 ===");
        List<User> editUser = userService.getAllUsers();
        for(User user : editUser){
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        //유저 이름 변경
        System.out.println("\n=== 유저 이름 변경 ===");
        userService.updateUserName(basicUser2.getId(), "김동명");
        userService.updateUserName(basicUser4.getId(), "박명동");

        //유저 삭제
        System.out.println("\n=== 유저 삭제 ===");
        userService.deleteUserById(basicUser5.getId());

        //전체 조회
        System.out.println("\n=== 삭제된 결과 조회 ===");
        List<User> lastUser = userService.getAllUsers();
        for(User user : lastUser){
            System.out.println("ID: " + user.getId() + ", 이름: " + user.getName() + ", 유저 생성 시점: " + user.getCreatedAt() + ", 유저 활동 상태: " + user.getConnectState());
        }

        return userService.getAllUsers();
    }

    
    // 채널 서비스 테스트
    private static List<Channel> channelManagementTest(ChannelService channelService, List<User> users) {

        try {
            System.out.println("\n"+
                    "============================================================================================================\n" +
                    "   ####   #    #     ##     #    #   #    #   ######   #                 ######   ######    ####     #####  \n" +
                    "  ##  ##  #    #    #  #    ##   #   ##   #   #        #                 # ## #   #        #    #      #    \n" +
                    " ##       #    #   #    #   # #  #   # #  #   #        #                   ##     #        #           #    \n" +
                    " ##       ######   ######   #  # #   #  # #   ####     #                   ##     ####      ####       #    \n" +
                    " ##       #    #   #    #   #   ##   #   ##   #        #                   ##     #             #      #    \n" +
                    "  ##  ##  #    #   #    #   #    #   #    #   #        #                   ##     #        #    #      #    \n" +
                    "   ####   #    #   #    #   #    #   #    #   ######   ######             ####    ######    ####       #    \n" +
                    "============================================================================================================");
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            System.err.println("대기 중 인터럽트가 발생했습니다.: " + e.getMessage());
        }

        // 채널 생성
        System.out.println("\n=== 채널 생성 ===");
        Channel basicChannel1 = channelService.createChannel("첫번째 채널", users.get(0).getId(), true, "0000");
        Channel basicChannel2 = channelService.createChannel("두번째 채널", users.get(1).getId(), true, "1111");
        Channel basicChannel3 = channelService.createChannel("세번째 채널", users.get(1).getId(), false, "");
        Channel basicChannel4 = channelService.createChannel("네번째 채널", users.get(0).getId(), false, "");

        // 생성된 결과 확인
        System.out.println("\n=== 생성 결과 확인 ===");
        List<Channel> allChannels = channelService.getAllChannels();
        for (Channel channel : allChannels){
            System.out.println("채널 이름: " + channel.getChannelName() + ", 비공개 채널: " + channel.isLock());
        }

        // 채널 이름 수정
        System.out.println("\n=== 채널 이름 수정 ===");
        channelService.updateChannelName(basicChannel1.getId(), users.get(0).getId(), "0000", "나의 첫번째 채널(수정됨)");

        // 이름 수정 결과 확인
        System.out.println("\n=== 수정 결과 확인 ===");
        List<Channel> resultUpdateChannelName = channelService.getAllChannels();
        for (Channel channel : resultUpdateChannelName){
            System.out.println("채널 이름: " + channel.getChannelName() +  ", 비공개 채널: " + channel.isLock());
        }

        // 채널에 유저 추가 (비밀번호 일치)
        System.out.println("\n=== 채널에 유저 추가 ===");
        channelService.addUserToChannel(basicChannel1.getId(), users.get(2).getId(), "0000");
        channelService.addUserToChannel(basicChannel1.getId(), users.get(3).getId(), "0000");
        channelService.addUserToChannel(basicChannel2.getId(), users.get(0).getId(), "1111");
        channelService.addUserToChannel(basicChannel3.getId(), users.get(2).getId(), "");
        channelService.addUserToChannel(basicChannel3.getId(), users.get(3).getId(), "");

        // 채널에 유저 추가 (비밀번호 불일치)
        System.out.println("\n===채널에 유저 추가 (비밀번호 일치하지 않는 경우) ===");
        channelService.addUserToChannel(basicChannel2.getId(), users.get(2).getId(), "0000");

        // 채널 참여 결과 확인
        System.out.println("\n=== 추가된 유저 확인 ===");
        List<Channel> resultAddParticipate = channelService.getAllChannels();
        for (Channel channel : resultAddParticipate){
            System.out.println("채널 이름: " + channel.getChannelName() +  ", 참여자: " + channel.getJoiningUsers());
        }

        // 채널 공개 상태 수정 (동일하게 수정하려고 할 때)
        System.out.println("\n=== 채널 공개 상태 수정 (동일한 상태 입력 시) ===");
        channelService.updateChannelPrivateState(basicChannel4.getId(), users.get(0).getId(), "", false);

        // 채널 공개 상태 수정
        System.out.println("\n=== 채널 공개 상태 수정 ===");
        channelService.updateChannelPrivateState(basicChannel4.getId(), users.get(0).getId(), "2222", true);

        // 결과 조회
        System.out.println("\n=== 변경된 결과 확인 ===");
        List<Channel> resultUpdateLockState = channelService.getAllChannels();
        for (Channel channel : resultUpdateLockState){
            System.out.println("채널 이름: " + channel.getChannelName() +  ", 비공개 채널: " + channel.isLock());
        }

        //채널 삭제(관리자가 아닌 경우)
        System.out.println("\n=== 채널 삭제 (권한 없는 경우) ===");
        channelService.deleteChannel(basicChannel3.getId(), users.get(3).getId(), "");

        // 채널 삭제 결과 확인
        System.out.println("\n=== 삭제 됐는지 확인 ===");
        List<Channel> resultRemoveChannelFalse = channelService.getAllChannels();
        for (Channel channel : resultRemoveChannelFalse){
            System.out.println("채널 이름: " + channel.getChannelName() +  ", 비공개 채널: " + channel.isLock());
        }

        //채널 삭제(관리자의 경우)
        System.out.println("\n=== 채널 삭제 ===");
        channelService.deleteChannel(basicChannel4.getId(), users.get(0).getId(), "2222");

        // 채널 삭제 결과 확인
        System.out.println("\n=== 삭제 결과 확인 ===");
        List<Channel> resultRemoveChannel = channelService.getAllChannels();
        for (Channel channel : resultRemoveChannel){
            System.out.println("채널 이름: " + channel.getChannelName() +  ", 비공개 채널: " + channel.isLock());
        }

        return channelService.getAllChannels();
    }


    // 메시지 서비스 테스트
    private static void messageManagementTest(MessageService messageService, List<User> users, List<Channel> channels){

        try {
            System.out.println("\n" +
                    "=========================================================================================================== \n" +
                    " ##   ##  ######    ####     ####      ##       ###    ######            ######   ######    ####     #####  \n" +
                    " ### ###  #        #    #   #    #    #  #     #   #   #                 # ## #   #        #    #      #    \n" +
                    " #######  #        #        #        #    #   #        #                   ##     #        #           #    \n" +
                    " ## # ##  ####      ####     ####    ######   #  ###   ####                ##     ####      ####       #    \n" +
                    " ##   ##  #             #        #   #    #   #    #   #                   ##     #             #      #    \n" +
                    " ##   ##  #        #    #   #    #   #    #    #   #   #                   ##     #        #    #      #    \n" +
                    " ##   ##  ######    ####     ####    #    #     ###    ######             ####    ######    ####       #    \n" +
                    "===========================================================================================================");
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            System.err.println("대기 중 인터럽트가 발생했습니다.: " + e.getMessage());
        }

        // 메시지 생성
        System.out.println("\n=== 메시지 생성 ===");
        Message basicMessage1 = messageService.CreateMessage(channels.get(0).getId(), "0000", users.get(2).getId(), "첫 메시지다!");
        Message basicMessage2 = messageService.CreateMessage(channels.get(0).getId(), "0000", users.get(3).getId(), "안녕하세요~");
        Message basicMessage3 = messageService.CreateMessage(channels.get(1).getId(), "1111", users.get(1).getId(), "반갑습니다.");
        Message basicMessage4 = messageService.CreateMessage(channels.get(1).getId(), "1111", users.get(0).getId(), "잘부탁드려요~");
        Message basicMessage5 = messageService.CreateMessage(channels.get(2).getId(), "", users.get(2).getId(), "오늘 하루는 어떠셨나요?");
        Message basicMessage6 = messageService.CreateMessage(channels.get(2).getId(), "", users.get(0).getId(), "이 채널은 처음이네요~");

        // 전체 메시지 조회
        System.out.println("\n=== 생성된 결과 조회 ===");
        List<Message> firstMessages = messageService.getAllMessage();
        for(Message message : firstMessages){
            System.out.println("채널 id: " + message.getChannelId() + ", 보낸 유저 id: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        // 메시지 수정
        System.out.println("\n=== 메시지 수정 ===");
        messageService.updateMessage(channels.get(1).getId(), "1111", basicMessage3.getMessageId(), users.get(1).getId(), "메시지를 수정해봤어요.");

        // 메시지 조회
        System.out.println("\n=== 수정된 결과 조회 ===");
        List<Message> editedMessages = messageService.getAllMessage();
        for(Message message : editedMessages){
            System.out.println("채널 id: " + message.getChannelId() + ", 보낸 유저 id: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        // 메시지 단건 조회
        System.out.println("\n=== 특정 메시지 조회 ===");
        Message messageFounbById = messageService.getMessageById(basicMessage4.getChannelId(), users.get(0).getId(), "1111", basicMessage4.getMessageId());
        System.out.println("채널 id: " + messageFounbById.getChannelId() + ", 보낸 유저 id: " + messageFounbById.getSenderId() + ", 메시지 내용: " + messageFounbById.getMessageContent());

        // 메시지 발송자를 이용한 조회
        System.out.println("\n=== 특정 유저의 메시지 조회 ===");
        List<Message> messageFoundBySenderId = messageService.userMessage(users.get(2).getId(), "0000");
        for(Message message : messageFoundBySenderId){
            System.out.println("채널 id: " + message.getChannelId() + ", 보낸 유저 id: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }

        // 메시지 삭제
        System.out.println("\n=== 메시지 삭제 ===");
        messageService.deletedMessage(basicMessage2.getMessageId(), users.get(3).getId(), "0000");

        // 삭제된 결과 조회
        System.out.println("\n=== 삭제된 결과 확인 ===");
        List<Message> lastMessage = messageService.getAllMessage();
        for(Message message : lastMessage){
            System.out.println("채널 id: " + message.getChannelId() + ", 보낸 유저 id: " + message.getSenderId() + ", 메시지 내용: " + message.getMessageContent());
        }
    }


    // 파일 삭제 메서드
    private static void deleteFileIfExists(String filePath) {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                System.out.println("ℹ 파일이 존재하지 않음: " + filePath);
            }

            if (file.delete()) {
                System.out.println(filePath + "파일 삭제됨 ");
            }
            else {
                System.out.println(filePath + "파일 삭제 실패 ");
            }
        }
        catch (Exception e) {
            System.err.println(filePath + "파일 삭제 중 오류 발생: ");
            e.printStackTrace();
        }
    }

}
