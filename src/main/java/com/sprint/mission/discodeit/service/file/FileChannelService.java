package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    //레포지토리 의존성
    private final ChannelRepository fileChannelRepository;

    public FileChannelService(ChannelRepository fileChannelRepository) {
        this.fileChannelRepository = fileChannelRepository;
    }

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID adminId, boolean lockState, String password) {
        System.out.println("채널 생성중");
        Channel newChannel = new Channel(channelName, adminId, lockState, password);
        fileChannelRepository.saveChannel(newChannel);
        // 채널 생성과 동시에 참여자에 관리자도 추가
        fileChannelRepository.addUserToChannel(newChannel.getId(), adminId);

        return newChannel;
    }

    // 모든 채널 조회
    @Override
    public List<Channel> getAllChannels() {
        System.out.println("\n전체 유저 조회: ");

        return fileChannelRepository.findAllChannels();
    }

    // 이름만으로 채널 조회
    @Override
    public List<Channel> getChannelUsingName(String channelName) {
        System.out.println("\n이름으로 채널 조회: ");

        return fileChannelRepository.findChannelUsingName(channelName);
    }

    // id로 채널 조회
    @Override
    public Channel getChannelUsingId(UUID channelId) {
        System.out.println("\n아이디로 채널 조회: ");

        return fileChannelRepository.findChannelUsingId(channelId);
    }


    // 채널 이름 수정
    @Override
    public boolean updateChannelName(UUID channelId, UUID userId, String password, String newChannelName) {
        System.out.println("\n채널 이름 수정: ");

        Channel channel = fileChannelRepository.findChannelUsingId(channelId);

        //채널 유효성 검사
        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            fileChannelRepository.updateChannelName(channelId, newChannelName);
            System.out.println("채널 이름 수정이 완료되었습니다.");

            return true;
        }
        return false;
    }

    //채널 공개/비공개 상태 수정
    @Override
    public boolean updateChannelPrivateState(UUID channelId, UUID userId, String password, boolean lockState) {
        System.out.println("\n채널 공개/비공개 상태 수정");

        Channel channel = fileChannelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel)) && (isChannelAdmin(channel, userId))) {

            if ((channel.isLock()) == lockState) {
                System.out.println("수정할 사항이 없습니다.");
                return false;
            }
            if ((channel.isLock())) {
                System.out.println("비밀번호 확인중입니다.");
                if (!Objects.equals(channel.getPassword(), password)) {
                    System.out.println("비밀번호가 일치하지 않습니다.");
                    return false;
                }
                System.out.println("공개 상태로 전환됩니다.");
                fileChannelRepository.channelUnLocking(channelId);
            } else {
                System.out.println("비공개 상태로 전환됩니다.");
                fileChannelRepository.channelLocking(channelId, password);
            }
            System.out.println("채널 공개 상태가 수정되었습니다.");
            return true;
        }

        return false;

    }

    // 채널 삭제
    @Override
    public boolean deleteChannel(UUID channelId, UUID userId, String password) {
        System.out.println("\n채널 삭제: ");

        Channel channel = fileChannelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            fileChannelRepository.deleteChannel(channelId);
            System.out.println("채널이 삭제되었습니다.");

            return true;
        }
        return false;
    }

    // 채널에 유저 추가
    @Override
    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {

        System.out.println("\n채널에 참여 유저 추가: ");

        Channel channel = fileChannelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel))&& (isChannelLock(channel, password))) {

            fileChannelRepository.addUserToChannel(channelId, userId);
            System.out.println(channel.getChannelName() + "채널에" + userId + " 유저가 추가되었습니다");

            return true;
        }

        return false;
    }

    // 참여한 유저 삭제
    @Override
    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {

        System.out.println("\n채널에 참여한 유저 삭제: ");

        Channel channel = fileChannelRepository.findChannelUsingId(channelId);

        if((isChannelExist(channel))&& (isChannelAdmin(channel, userId)) && (isChannelLock(channel, password))) {

            fileChannelRepository.deleteUserInChannel(channelId, userId);
            System.out.println(userId + " 유저가 삭제되었습니다");

            return true;
        }
        return false;
    }

    // 채널 유효성 검사 - 존재여부
    private boolean isChannelExist(Channel channel){
        if(channel == null){
            System.out.println("채널이 존재하지 않습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 관리자 대조
    private boolean isChannelAdmin(Channel channel, UUID userId){
        if(!channel.getAdminId().equals(userId)){
            System.out.println("채널 정보 수정 권한이 없습니다.");
            return false;
        }
        return true;
    }
    // 채널 유효성 검사 - 비밀번호 대조
    private boolean isChannelLock(Channel channel, String password){
        if(channel.isLock()){
            System.out.println("비공개 채널입니다. 비밀번호를 확인중입니다.");
            if(!channel.getPassword().equals(password)){
                System.out.println("비밀번호가 일치하지 않습니다.");
                return false;
            }
        }
        return true;
    }
}
