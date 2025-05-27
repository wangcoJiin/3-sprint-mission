//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class JCFChannelService implements ChannelService {
//
//    private Map<UUID, Channel> channels = new LinkedHashMap<>();
//
//    //레포지토리 의존성
//    private final ChannelRepository jcfChannelRepository;
//
//    public JCFChannelService(ChannelRepository jcfChannelRepository) {
//        this.jcfChannelRepository = jcfChannelRepository;
//    }
//
//    // 채널 생성
//    @Override
//    public Channel createChannel(String channelName, UUID adminId, boolean lockState, String password) {
//        System.out.println("채널 생성중");
//        Channel newChannel = new Channel(channelName, adminId, lockState, password);
//        jcfChannelRepository.saveChannel(newChannel);
//        // 채널 생성과 동시에 참여자에 관리자도 추가
//        jcfChannelRepository.addUserToChannel(newChannel.getId(), adminId);
//
//        return newChannel;
//    }
//
//    // 모든 채널 조회
//    @Override
//    public List<Channel> getAllChannels() {
//        System.out.println("\n전체 유저 조회: ");
//
//        return jcfChannelRepository.findAllChannels();
//    }
//
//    // 이름만으로 채널 조회
//    @Override
//    public List<Channel> getChannelUsingName(String channelName) {
//        System.out.println("\n이름으로 채널 조회: ");
//
//        return jcfChannelRepository.findChannelUsingName(channelName);
//    }
//
//    // id로 채널 조회
//    @Override
//    public Channel getChannelUsingId(UUID channelId) {
//        System.out.println("\n아이디로 채널 조회: ");
//
//        return jcfChannelRepository.findChannelUsingId(channelId);
//    }
//
//
//    // 채널 이름 수정
//    @Override
//    public boolean updateChannelName(UUID channelId, UUID userId, String password, String newChannelName) {
//        System.out.println("\n채널 이름 수정: ");
//
//        Channel channel = jcfChannelRepository.findChannelUsingId(channelId);
//
//        if (channel == null) {
//            System.out.println("채널이 존재하지 않습니다.");
//            return false;
//        }
//
//        if (channel.getAdminId() != userId) {
//            System.out.println("채널 이름 수정 권한이 없습니다.");
//            return false;
//        }
//        System.out.println("채널 관리자로 확인되었습니다.");
//
//        // 비공개 채널인 경우
//        if ((channel.isLock())) {
//            System.out.println("비밀번호 확인중입니다.");
//            if (!Objects.equals(channel.getPassword(), password)) {
//                System.out.println("비밀번호가 일치하지 않습니다.");
//                return false;
//            }
//        }
//
//        jcfChannelRepository.updateChannelName(channelId, newChannelName);
//        System.out.println("채널 이름 수정이 완료되었습니다.");
//
//        return true;
//    }
//
//    //채널 공개/비공개 상태 수정
//    @Override
//    public boolean updateChannelPrivateState(UUID channelId, UUID userId, String password, boolean lockState) {
//        System.out.println("\n채널 공개/비공개 상태 수정");
//
//        Channel channel = jcfChannelRepository.findChannelUsingId(channelId);
//
//        if (channel == null) {
//            System.out.println("채널이 존재하지 않습니다.");
//            return false;
//        }
//
//        if (channel.getAdminId() != userId) {
//            System.out.println("채널 상태 수정 권한이 없습니다.");
//            return false;
//        }
//            System.out.println("채널 관리자로 확인되었습니다.");
//
//        if ((channel.isLock()) == lockState) {
//            System.out.println("수정할 사항이 없습니다.");
//            return false;
//        }
//
//        if ((channel.isLock())) {
//            System.out.println("비밀번호 확인중입니다.");
//            if (!Objects.equals(channel.getPassword(), password)) {
//                System.out.println("비밀번호가 일치하지 않습니다.");
//                return false;
//            }
//            System.out.println("공개 상태로 전환됩니다.");
//            jcfChannelRepository.channelUnLocking(channelId);
//        }
//        else {
//            System.out.println("비공개 상태로 전환됩니다.");
//            jcfChannelRepository.channelLocking(channelId, password);
//        }
//        System.out.println("채널 공개 상태가 수정되었습니다.");
//        return true;
//
//    }
//
//    // 채널 삭제
//    @Override
//    public boolean deleteChannel(UUID channelId, UUID userId, String password) {
//        System.out.println("\n채널 삭제: ");
//
//        Channel channel = jcfChannelRepository.findChannelUsingId(channelId);
//
//        if (channel == null) {
//            System.out.println("채널이 존재하지 않습니다.");
//            return false;
//        }
//
//        if (channel.getAdminId() != userId) {
//            System.out.println("채널 삭제 권한이 없습니다.");
//            return false;
//        }
//        System.out.println("채널 관리자로 확인되었습니다.");
//
//        if ((channel.isLock())) {
//            System.out.println("비밀번호 확인중입니다.");
//            if (!Objects.equals(channel.getPassword(), password)) {
//                System.out.println("비밀번호가 일치하지 않습니다.");
//                return false;
//            }
//        }
//
//        jcfChannelRepository.deleteChannel(channelId);
//        System.out.println("채널이 삭제되었습니다.");
//
//        return true;
//    }
//
//    // 채널에 유저 추가
//    @Override
//    public boolean addUserToChannel(UUID channelId, UUID userId, String password) {
//
//        System.out.println("\n채널에 참여 유저 추가: ");
//
//        Channel channel = jcfChannelRepository.findChannelUsingId(channelId);
//
//        if (channel == null) {
//            System.out.println("채널이 존재하지 않습니다.");
//            return false;
//        }
//
//        if ((channel.isLock())) {
//            System.out.println("비공개 채널입니다.");
//            System.out.println("비밀번호 확인중입니다.");
//
//            if (!Objects.equals(channel.getPassword(), password)) {
//                System.out.println("비밀번호가 일치하지 않습니다.");
//                return false;
//            }
//        }
//        jcfChannelRepository.addUserToChannel(channelId, userId);
//        System.out.println(channel.getChannelName() + "채널에" + userId + " 유저가 추가되었습니다");
//
//        return true;
//    }
//
//    // 참여한 유저 삭제
//    @Override
//    public boolean deleteUserInChannel(UUID channelId, UUID adminId, UUID userId, String password) {
//
//        System.out.println("\n채널에 참여한 유저 삭제: ");
//
//        Channel channel = jcfChannelRepository.findChannelUsingId(channelId);
//
//        if (channel == null) {
//            System.out.println("채널이 존재하지 않습니다.");
//            return false;
//        }
//
//        if (channel.getAdminId() != adminId) {
//            System.out.println("유저 삭제 권한이 없습니다.");
//            return false;
//        }
//        System.out.println("채널 관리자로 확인되었습니다.");
//
//        if ((channel.isLock())) {
//            System.out.println("비공개 채널입니다.");
//            System.out.println("비밀번호 확인중입니다.");
//            if (!Objects.equals(channel.getPassword(), password)) {
//                System.out.println("비밀번호가 일치하지 않습니다.");
//                return false;
//            }
//        }
//
//        jcfChannelRepository.deleteUserInChannel(channelId, userId);
//        System.out.println(userId + " 유저가 삭제되었습니다");
//
//        return true;
//    }
//}